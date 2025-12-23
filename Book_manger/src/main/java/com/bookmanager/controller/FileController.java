package com.bookmanager.controller;

import com.bookmanager.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@Api(tags = "文件上传")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @ApiOperation("上传图片")
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        // TODO: 实现文件上传
        return Result.success();
    }

    @ApiOperation("上传图书封面")
    @PostMapping("/upload/cover")
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file) {
        // TODO: 实现图书封面上传
        return Result.success();
    }

    @ApiOperation("上传用户头像")
    @PostMapping("/upload/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        // TODO: 实现用户头像上传
        return Result.success();
    }
}

