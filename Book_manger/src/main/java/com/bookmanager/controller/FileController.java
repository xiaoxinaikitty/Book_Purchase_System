package com.bookmanager.controller;

import com.bookmanager.common.Result;
import com.bookmanager.exception.BusinessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Api(tags = "文件上传")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @ApiOperation("上传图片")
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return doUpload(file, "common");
    }

    @ApiOperation("上传图书封面")
    @PostMapping("/upload/cover")
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file) {
        return doUpload(file, "cover");
    }

    @ApiOperation("上传用户头像")
    @PostMapping("/upload/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return doUpload(file, "avatar");
    }

    private Result<String> doUpload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || !isImage(contentType)) {
            throw new BusinessException("仅支持图片上传");
        }

        String originalName = file.getOriginalFilename();
        String ext = getFileExtension(originalName, contentType);
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        File dir = new File(uploadPath, folder);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BusinessException("创建上传目录失败");
        }

        File target = new File(dir, fileName);
        try {
            file.transferTo(target);
        } catch (IOException e) {
            throw new BusinessException("上传失败，请重试");
        }

        String url = "/uploads/" + folder + "/" + fileName;
        return Result.success("上传成功", url);
    }

    private boolean isImage(String contentType) {
        String type = contentType.toLowerCase(Locale.ROOT);
        return type.equals("image/jpeg")
                || type.equals("image/png")
                || type.equals("image/gif")
                || type.equals("image/webp");
    }

    private String getFileExtension(String originalName, String contentType) {
        if (originalName != null && originalName.contains(".")) {
            return originalName.substring(originalName.lastIndexOf(".")).toLowerCase(Locale.ROOT);
        }
        String type = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        switch (type) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "image/webp":
                return ".webp";
            default:
                return ".png";
        }
    }
}

