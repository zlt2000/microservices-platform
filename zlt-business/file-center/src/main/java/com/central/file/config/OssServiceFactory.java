package com.central.file.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.central.file.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.central.file.model.FileType;

/**
 * @author zlt
 * FileService工厂<br>
 * 将各个实现类放入map
 */
@Configuration
public class OssServiceFactory {

    private Map<FileType, IFileService> map = new HashMap<>();
    @Autowired
    private IFileService aliyunOssServiceImpl;

    @Autowired
    private IFileService qiniuOssServiceImpl;

    @PostConstruct
    public void init() {
        map.put(FileType.ALIYUN, aliyunOssServiceImpl);
        map.put(FileType.QINIU, qiniuOssServiceImpl);
    }

    public IFileService getFileService(String fileType) {
        return map.get(FileType.valueOf(fileType));
    }
}
