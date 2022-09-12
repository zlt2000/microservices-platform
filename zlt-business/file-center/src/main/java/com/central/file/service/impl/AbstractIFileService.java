package com.central.file.service.impl;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.central.oss.model.ObjectInfo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.multipart.MultipartFile;

import com.central.common.model.PageResult;
import com.central.file.mapper.FileMapper;
import com.central.file.model.FileInfo;
import com.central.file.service.IFileService;
import com.central.file.utils.FileUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * AbstractIFileService 抽取类
 * 根据zlt.file-server.type 实例化具体对象
 *
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@Slf4j
public abstract class AbstractIFileService extends ServiceImpl<FileMapper, FileInfo> implements IFileService {
    private static final String FILE_SPLIT = ".";

    @Override
    public FileInfo upload(MultipartFile file) {
        FileInfo fileInfo = FileUtil.getFileInfo(file);
        if (!fileInfo.getName().contains(FILE_SPLIT)) {
            throw new IllegalArgumentException("缺少后缀名");
        }
        ObjectInfo objectInfo = uploadFile(file);
        fileInfo.setPath(objectInfo.getObjectPath());
        fileInfo.setUrl(objectInfo.getObjectUrl());
        // 设置文件来源
        fileInfo.setSource(fileType());
        // 将文件信息保存到数据库
        baseMapper.insert(fileInfo);

        return fileInfo;
    }

    /**
     * 文件来源
     *
     * @return
     */
    protected abstract String fileType();

    /**
     * 上传文件
     *
     * @param file
     */
    protected abstract ObjectInfo uploadFile(MultipartFile file);

    /**
     * 删除文件
     * @param id 文件id
     */
    @Override
    public void delete(String id) {
        FileInfo fileInfo = baseMapper.selectById(id);
        if (fileInfo != null) {
            baseMapper.deleteById(fileInfo.getId());
            this.deleteFile(fileInfo.getPath());
        }
    }

    /**
     * 删除文件资源
     *
     * @param objectPath 文件路径
     */
    protected abstract void deleteFile(String objectPath);

    @Override
    public PageResult<FileInfo> findList(Map<String, Object> params) {
        Page<FileInfo> page = new Page<>(MapUtils.getInteger(params, "page"), MapUtils.getInteger(params, "limit"));
        List<FileInfo> list = baseMapper.findList(page, params);
        return PageResult.<FileInfo>builder().data(list).code(0).count(page.getTotal()).build();
    }
}
