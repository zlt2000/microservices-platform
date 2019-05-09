package com.central.file.config;

import com.central.file.model.FileInfo;
import com.central.file.properties.FileServerProperties;
import com.central.file.service.impl.AbstractIFileService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 七牛云配置
 *
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@Configuration
@ConditionalOnProperty(name = "zlt.file-server.type", havingValue = "qiniu")
public class QiniuOSSAutoConfigure {
    @Autowired
    private FileServerProperties fileProperties;

    /**
     * 华东机房
     */
    @Bean
    public com.qiniu.storage.Configuration qiniuConfig() {
        return new com.qiniu.storage.Configuration(Zone.zone2());
    }

    /**
     * 构建一个七牛上传工具实例
     */
    @Bean
    public UploadManager uploadManager() {
        return new UploadManager(qiniuConfig());
    }

    /**
     * 认证信息实例
     *
     * @return
     */
    @Bean
    public Auth auth() {
        return Auth.create(fileProperties.getOss().getAccessKey(), fileProperties.getOss().getAccessKeySecret());
    }

    /**
     * 构建七牛空间管理实例
     */
    @Bean
    public BucketManager bucketManager() {
        return new BucketManager(auth(), qiniuConfig());
    }

    @Service
    public class QiniuOssServiceImpl extends AbstractIFileService {
        @Autowired
        private UploadManager uploadManager;
        @Autowired
        private BucketManager bucketManager;
        @Autowired
        private Auth auth;

        @Override
        protected String fileType() {
            return fileProperties.getType();
        }

        @Override
        protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
            // 调用put方法上传
            uploadManager.put(file.getBytes(), fileInfo.getName(), auth.uploadToken(fileProperties.getOss().getBucketName()));
            fileInfo.setUrl(fileProperties.getOss().getEndpoint() + "/" + fileInfo.getName());
            fileInfo.setPath(fileProperties.getOss().getEndpoint() + "/" + fileInfo.getName());
        }

        @Override
        protected boolean deleteFile(FileInfo fileInfo) {
            try {
                Response response = bucketManager.delete(fileProperties.getOss().getBucketName(), fileInfo.getPath());
                int retry = 0;
                while (response.needRetry() && retry++ < 3) {
                    response = bucketManager.delete(fileProperties.getOss().getBucketName(), fileInfo.getPath());
                }
            } catch (QiniuException e) {
                return false;
            }
            return true;
        }
    }
}
