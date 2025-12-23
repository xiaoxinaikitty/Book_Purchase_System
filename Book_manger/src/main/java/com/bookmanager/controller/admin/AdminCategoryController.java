package com.bookmanager.controller.admin;

import com.bookmanager.common.Result;
import com.bookmanager.entity.Category;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 分类管理控制器（管理员端）
 */
@Api(tags = "分类管理-管理员端")
@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryController {

    @ApiOperation("获取分类列表")
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

    @ApiOperation("添加分类")
    @PostMapping("/add")
    public Result<Void> addCategory(@Valid @RequestBody Category category) {
        // TODO: 实现添加分类
        return Result.success();
    }

    @ApiOperation("更新分类")
    @PutMapping("/update")
    public Result<Void> updateCategory(@Valid @RequestBody Category category) {
        // TODO: 实现更新分类
        return Result.success();
    }

    @ApiOperation("删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        // TODO: 实现删除分类
        return Result.success();
    }
}

