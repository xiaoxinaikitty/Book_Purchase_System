package com.bookmanager.controller.admin;

import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.entity.Book;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 图书管理控制器（管理员端）
 */
@Api(tags = "图书管理-管理员端")
@RestController
@RequestMapping("/api/admin/book")
public class AdminBookController {

    @ApiOperation("获取图书列表（分页）")
    @GetMapping("/list")
    public Result<PageResult<Book>> getBookList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        // TODO: 实现获取图书列表
        return Result.success();
    }

    @ApiOperation("获取图书详情")
    @GetMapping("/{id}")
    public Result<Book> getBookDetail(@PathVariable Long id) {
        // TODO: 实现获取图书详情
        return Result.success();
    }

    @ApiOperation("添加图书")
    @PostMapping("/add")
    public Result<Void> addBook(@Valid @RequestBody Book book) {
        // TODO: 实现添加图书
        return Result.success();
    }

    @ApiOperation("更新图书")
    @PutMapping("/update")
    public Result<Void> updateBook(@Valid @RequestBody Book book) {
        // TODO: 实现更新图书
        return Result.success();
    }

    @ApiOperation("删除图书")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable Long id) {
        // TODO: 实现删除图书
        return Result.success();
    }

    @ApiOperation("上架/下架图书")
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @ApiParam("状态 0-下架 1-上架") @RequestParam Integer status) {
        // TODO: 实现上架/下架图书
        return Result.success();
    }
}

