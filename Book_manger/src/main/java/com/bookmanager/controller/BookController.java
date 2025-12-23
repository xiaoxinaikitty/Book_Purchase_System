package com.bookmanager.controller;

import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.entity.Book;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书控制器（用户端）
 */
@Api(tags = "图书管理-用户端")
@RestController
@RequestMapping("/api/book")
public class BookController {

    @ApiOperation("获取图书列表（分页）")
    @GetMapping("/list")
    public Result<PageResult<Book>> getBookList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        // TODO: 实现获取图书列表
        return Result.success();
    }

    @ApiOperation("获取图书详情")
    @GetMapping("/detail/{id}")
    public Result<Book> getBookDetail(@PathVariable Long id) {
        // TODO: 实现获取图书详情
        return Result.success();
    }

    @ApiOperation("搜索图书")
    @GetMapping("/search")
    public Result<PageResult<Book>> searchBooks(
            @ApiParam("关键词") @RequestParam String keyword,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size) {
        // TODO: 实现搜索图书
        return Result.success();
    }

    @ApiOperation("获取热门图书")
    @GetMapping("/hot")
    public Result<List<Book>> getHotBooks(
            @ApiParam("数量") @RequestParam(defaultValue = "10") Integer limit) {
        // TODO: 实现获取热门图书
        return Result.success();
    }

    @ApiOperation("获取新书推荐")
    @GetMapping("/new")
    public Result<List<Book>> getNewBooks(
            @ApiParam("数量") @RequestParam(defaultValue = "10") Integer limit) {
        // TODO: 实现获取新书推荐
        return Result.success();
    }
}

