package com.central.oss.template;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.central.oss.model.ObjectInfo;
import com.central.oss.properties.FileServerProperties;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;

/**
 * aws s3配置
 *
 * @author zlt
 * @date 2021/2/11
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@ConditionalOnClass(AmazonS3.class)
@ConditionalOnProperty(prefix = FileServerProperties.PREFIX, name = "type", havingValue = FileServerProperties.TYPE_S3)
public class S3Template implements InitializingBean {
    private static final String DEF_CONTEXT_TYPE = "application/octet-stream";
    private static final String PATH_SPLIT = "/";

    @Autowired
    private FileServerProperties fileProperties;

    private AmazonS3 amazonS3;

    @Override
    public void afterPropertiesSet() {
        ClientConfiguration config = new ClientConfiguration();
        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(fileProperties.getS3().getEndpoint(), fileProperties.getS3().getRegion());
        AWSCredentials credentials = new BasicAWSCredentials(fileProperties.getS3().getAccessKey(), fileProperties.getS3().getAccessKeySecret());
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(credentials);
        this.amazonS3 = AmazonS3Client.builder()
                .withEndpointConfiguration(endpoint)
                .withClientConfiguration(config)
                .withCredentials(awsCredentialsProvider)
                .withPathStyleAccessEnabled(fileProperties.getS3().getPathStyleAccessEnabled())
                .disableChunkedEncoding()
                .build();
    }

    @SneakyThrows
    public ObjectInfo upload(String fileName, InputStream is) {
        return upload(fileProperties.getS3().getBucketName(), fileName, is, is.available(), DEF_CONTEXT_TYPE);
    }

    @SneakyThrows
    public ObjectInfo upload(MultipartFile file) {
        return upload(fileProperties.getS3().getBucketName(), file.getOriginalFilename(), file.getInputStream()
                , ((Long)file.getSize()).intValue(), file.getContentType());
    }

    @SneakyThrows
    public ObjectInfo upload(String bucketName, String fileName, InputStream is) {
        return upload(bucketName, fileName, is, is.available(), DEF_CONTEXT_TYPE);
    }

    /**
     * 上传对象
     * @param bucketName bucket名称
     * @param objectName 对象名
     * @param is 对象流
     * @param size 大小
     * @param contentType 类型
     */
    private ObjectInfo upload(String bucketName, String objectName, InputStream is, int size, String contentType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(contentType);
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, objectName, is, objectMetadata);
        putObjectRequest.getRequestClientOptions().setReadLimit(size + 1);
        amazonS3.putObject(putObjectRequest);

        ObjectInfo obj = new ObjectInfo();
        obj.setObjectPath(bucketName + PATH_SPLIT + objectName);
        obj.setObjectUrl(fileProperties.getS3().getEndpoint() + PATH_SPLIT + obj.getObjectPath());
        return obj;
    }

    public void delete(String objectName) {
        delete(fileProperties.getS3().getBucketName(), objectName);
    }

    public void delete(String bucketName, String objectName) {
        amazonS3.deleteObject(bucketName, objectName);
    }

    /**
     * 获取预览地址
     * @param bucketName bucket名称
     * @param objectName 对象名
     * @param expires 有效时间(分钟)，最大7天有效
     * @return
     */
    public String getViewUrl(String bucketName, String objectName, int expires) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, expires);
        URL url = amazonS3.generatePresignedUrl(bucketName, objectName, cal.getTime());
        return url.toString();
    }

    public void out(String objectName, OutputStream os) {
        out(fileProperties.getS3().getBucketName(), objectName, os);
    }

    /**
     * 输出对象
     * @param bucketName bucket名称
     * @param objectName 对象名
     * @param os 输出流
     */
    @SneakyThrows
    public void out(String bucketName, String objectName, OutputStream os) {
        S3Object s3Object = amazonS3.getObject(bucketName, objectName);
        try (
                S3ObjectInputStream s3is = s3Object.getObjectContent();
        ) {
            IOUtils.copy(s3is, os);
        }
    }
}
