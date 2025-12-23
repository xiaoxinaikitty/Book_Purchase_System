package com.bookmanager.controller;

import com.bookmanager.common.Result;
import com.bookmanager.entity.Category;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@Api(tags = "图书分类")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @ApiOperation("获取所有分类")
    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        // TODO: 实现获取分类列表
        return Result.success();
    }

    @ApiOperation("获取分类树")
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        // TODO: 实现获取分类树
        return Result.success();
    }
}

