package com.bookmanager.controller.admin;

import com.bookmanager.common.Result;
import com.bookmanager.entity.Category;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.CategoryService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("获取分类列表")
    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        checkAdmin();
        return Result.success(categoryService.getAllCategories());
    }

    @ApiOperation("获取分类树")
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        checkAdmin();
        return Result.success(categoryService.getCategoryTree());
    }

    @ApiOperation("添加分类")
    @PostMapping("/add")
    public Result<Void> addCategory(@Valid @RequestBody Category category) {
        checkAdmin();
        boolean success = categoryService.addCategory(category);
        if (success) {
            return Result.success("添加成功", null);
        }
        return Result.error("添加失败");
    }

    @ApiOperation("更新分类")
    @PutMapping("/update")
    public Result<Void> updateCategory(@Valid @RequestBody Category category) {
        checkAdmin();
        boolean success = categoryService.updateCategory(category);
        if (success) {
            return Result.success("更新成功", null);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        checkAdmin();
        boolean success = categoryService.deleteCategory(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "无权限访问");
        }
    }
}

