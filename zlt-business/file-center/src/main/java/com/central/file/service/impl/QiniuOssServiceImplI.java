package com.central.file.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.central.file.model.FileInfo;
import com.central.file.model.FileType;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

/**
 * 七牛云oss存储文件
 *
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@Slf4j
@Service("qiniuOssServiceImpl")
public class QiniuOssServiceImplI extends AbstractIFileService implements InitializingBean {
    @Autowired
    private UploadManager uploadManager;
    @Autowired
    private BucketManager bucketManager;
    @Autowired
    private Auth auth;
    @Value("${qiniu.oss.bucketName:xxxxx}")
    private String bucket;
    @Value("${qiniu.oss.endpoint:xxxxx}")
    private String endpoint;
    private StringMap putPolicy;

    @Override
    protected FileType fileType() {
        return FileType.QINIU;
    }

    @Override
    protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
        // 调用put方法上传
        uploadManager.put(file.getBytes(), fileInfo.getName(), auth.uploadToken(bucket));
        fileInfo.setUrl(endpoint + "/" + fileInfo.getName());
        fileInfo.setPath(endpoint + "/" + fileInfo.getName());
    }

    @Override
    protected boolean deleteFile(FileInfo fileInfo) {
        try {
            Response response = bucketManager.delete(this.bucket, fileInfo.getPath());
            int retry = 0;
            while (response.needRetry() && retry++ < 3) {
                response = bucketManager.delete(bucket, fileInfo.getPath());
            }
        } catch (QiniuException e) {
            return false;
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.putPolicy = new StringMap();
        putPolicy.put("returnBody",
                "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
    }
}
