package com.central.file.config;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.central.file.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.central.file.model.FileType;

/**
 * FileService工厂<br>
 * 将各个实现类放入map
 *
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@Configuration
public class OssServiceFactory {

    private Map<FileType, IFileService> map = new EnumMap<>(FileType.class);
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
