package com.bookmanager.controller;

import com.bookmanager.common.Result;
import com.bookmanager.entity.Category;
import com.bookmanager.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@Api(tags = "图书分类")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("获取所有分类")
    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        return Result.success(categoryService.getAllCategories());
    }

    @ApiOperation("获取分类树")
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }
}

