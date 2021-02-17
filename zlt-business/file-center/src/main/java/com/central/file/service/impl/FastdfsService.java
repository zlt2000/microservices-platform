package com.central.file.service.impl;

import com.central.oss.model.ObjectInfo;
import com.central.oss.properties.FileServerProperties;
import com.central.oss.template.FdfsTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.OutputStream;

/**
 * @author zlt
 * @date 2021/2/13
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Service
@ConditionalOnProperty(prefix = com.central.oss.properties.FileServerProperties.PREFIX, name = "type", havingValue = FileServerProperties.TYPE_FDFS)
public class FastdfsService extends AbstractIFileService {
    @Resource
    private FdfsTemplate fdfsTemplate;

    @Override
    protected String fileType() {
        return FileServerProperties.TYPE_FDFS;
    }

    @Override
    protected ObjectInfo uploadFile(MultipartFile file) {
        return fdfsTemplate.upload(file);
    }

    @Override
    protected void deleteFile(String objectPath) {
        fdfsTemplate.delete(objectPath);
    }

    @Override
    public void out(String id, OutputStream os) {
    }
}
