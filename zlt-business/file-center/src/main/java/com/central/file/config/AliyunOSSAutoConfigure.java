package com.central.file.config;

import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.central.file.model.FileInfo;
import com.central.file.properties.FileServerProperties;
import com.central.file.service.impl.AbstractIFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.oss.OSSClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 阿里云配置
 *
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@Configuration
@ConditionalOnProperty(name = "zlt.file-server.type", havingValue = "aliyun")
public class AliyunOSSAutoConfigure {
    @Autowired
    private FileServerProperties fileProperties;

    /**
     * 阿里云文件存储client
     * 只有配置了aliyun.oss.access-key才可以使用
     */
    @Bean
    public OSSClient ossClient() {
        OSSClient ossClient = new OSSClient(fileProperties.getOss().getEndpoint()
                , new DefaultCredentialProvider(fileProperties.getOss().getAccessKey(), fileProperties.getOss().getAccessKeySecret())
                , null);
        return ossClient;
    }

    @Service
    public class AliyunOssServiceImpl extends AbstractIFileService {
        @Autowired
        private OSSClient ossClient;

        @Override
        protected String fileType() {
            return fileProperties.getType();
        }

        @Override
        protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
            ossClient.putObject(fileProperties.getOss().getBucketName(), fileInfo.getName(), file.getInputStream());
            fileInfo.setUrl(fileProperties.getOss().getDomain() + "/" + fileInfo.getName());
        }

        @Override
        protected boolean deleteFile(FileInfo fileInfo) {
            ossClient.deleteObject(fileProperties.getOss().getBucketName(), fileInfo.getName());
            return true;
        }
    }
}
