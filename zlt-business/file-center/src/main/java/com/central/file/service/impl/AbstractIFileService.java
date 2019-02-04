package com.central.file.service.impl;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.multipart.MultipartFile;

import com.central.common.model.PageResult;
import com.central.file.mapper.FileMapper;
import com.central.file.model.FileInfo;
import com.central.file.model.FileType;
import com.central.file.service.IFileService;
import com.central.file.utils.FileUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zlt
 * AbstractIFileService 抽取类
 * 根据filetype 实例化具体oss对象
*/
@Slf4j
public abstract class AbstractIFileService extends ServiceImpl<FileMapper, FileInfo> implements IFileService {
	@Override
	public FileInfo upload(MultipartFile file  ) throws Exception {
		FileInfo fileInfo = FileUtil.getFileInfo(file);
		FileInfo oldFileInfo = baseMapper.selectById(fileInfo.getId());
		if (oldFileInfo != null) {
			return oldFileInfo;
		}
		if (!fileInfo.getName().contains(".")) {
			throw new IllegalArgumentException("缺少后缀名");
		}
		uploadFile(file, fileInfo);
		fileInfo.setSource(fileType().name());// 设置文件来源
		baseMapper.insert(fileInfo);// 将文件信息保存到数据库

		return fileInfo;
	}

	/**
	 * 文件来源
	 * 
	 * @return
	 */
	protected abstract FileType fileType();

	/**
	 * 上传文件
	 * 
	 * @param file
	 * @param fileInfo
	 */
	protected abstract void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception;

	/**
	 * 删除文件资源
	 * 
	 * @param fileInfo
	 * @return
	 */
	protected abstract boolean deleteFile(FileInfo fileInfo);

	@Override
	public PageResult<FileInfo> findList(Map<String, Object> params){
		Page<FileInfo> page = new Page<>(MapUtils.getInteger(params, "page"), MapUtils.getInteger(params, "limit"));
        List<FileInfo> list = baseMapper.findList(page, params);
		return PageResult.<FileInfo>builder().data(list).code(0).count(page.getTotal()).build();
	}
}
