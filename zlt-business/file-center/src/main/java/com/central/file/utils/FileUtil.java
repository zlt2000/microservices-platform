package com.central.file.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import cn.hutool.core.util.IdUtil;
import com.central.file.model.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件工具类
 *
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@Slf4j
public class FileUtil {
	private FileUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static FileInfo getFileInfo(MultipartFile file) {
		FileInfo fileInfo = new FileInfo();
		fileInfo.setId(IdUtil.fastSimpleUUID());
		fileInfo.setName(file.getOriginalFilename());
		fileInfo.setContentType(file.getContentType());
		fileInfo.setIsImg(fileInfo.getContentType().startsWith("image/"));
		fileInfo.setSize(file.getSize());
		fileInfo.setCreateTime(new Date());
		return fileInfo;
	}

	/**
	 * 文件的md5
	 *
	 * @param inputStream
	 * @return
	 */
	public static String fileMd5(InputStream inputStream) {
		try {
			return DigestUtils.md5Hex(inputStream);
		} catch (IOException e) {
			log.error("fileMd5-error", e);
		}
		return null;
	}

	public static String saveFile(MultipartFile file, String path) {
		try {
			File targetFile = new File(path);
			if (targetFile.exists()) {
				return path;
			}
			if (!targetFile.getParentFile().exists()) {
				targetFile.getParentFile().mkdirs();
			}
			file.transferTo(targetFile);
			return path;
		} catch (Exception e) {
			log.error("saveFile-error", e);
		}
		return null;
	}

	public static boolean deleteFile(String pathname) {
		File file = new File(pathname);
		if (file.exists()) {
			boolean flag = file.delete();
			if (flag) {
				File[] files = file.getParentFile().listFiles();
				if (files == null || files.length == 0) {
					file.getParentFile().delete();
				}
			}
			return flag;
		}
		return false;
	}
}
