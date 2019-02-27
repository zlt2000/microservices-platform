package com.central.file.controller;

import java.util.Map;

import com.central.common.model.Result;
import com.central.file.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.central.common.model.PageResult;
import com.central.file.config.OssServiceFactory;
import com.central.file.model.FileInfo;
import com.central.file.model.FileType;

/**
 * 文件上传 同步oss db双写 目前仅实现了阿里云,七牛云
 * 参考src/main/view/upload.html
 *
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@RestController
public class FileController {

    @Autowired
    private OssServiceFactory fileServiceFactory;

    /**
     * 文件上传
     * 根据fileType选择上传方式
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/files-anon")
    public FileInfo upload(@RequestParam("file") MultipartFile file) throws Exception {
        String fileType = FileType.QINIU.toString();
        IFileService fileService = fileServiceFactory.getFileService(fileType);
        return fileService.upload(file);
    }

    /**
     * 文件删除
     *
     * @param id
     */
    @DeleteMapping("/files/{id}")
    public Result delete(@PathVariable String id) {
        try {
            FileInfo fileInfo = fileServiceFactory.getFileService(FileType.QINIU.toString()).getById(id);
            if (fileInfo != null) {
                IFileService fileService = fileServiceFactory.getFileService(fileInfo.getSource());
                fileService.removeById(fileInfo);
            }
            return Result.succeed("操作成功");
        } catch (Exception ex) {
            return Result.failed("操作失败");
        }
    }

    /**
     * 文件查询
     *
     * @param params
     * @return
     */
    @GetMapping("/files")
    public PageResult<FileInfo> findFiles(@RequestParam Map<String, Object> params) {
        return fileServiceFactory.getFileService(FileType.QINIU.toString()).findList(params);
    }
}
