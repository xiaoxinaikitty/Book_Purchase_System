package com.bookmanager.controller;

import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.dto.ReviewDTO;
import com.bookmanager.entity.Review;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 图书评价控制器
 */
@Api(tags = "图书评价")
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @ApiOperation("添加评价")
    @PostMapping("/add")
    public Result<Void> addReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        // TODO: 实现添加评价
        return Result.success();
    }

    @ApiOperation("获取图书评价列表")
    @GetMapping("/book/{bookId}")
    public Result<PageResult<Review>> getBookReviews(
            @PathVariable Long bookId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size) {
        // TODO: 实现获取图书评价列表
        return Result.success();
    }

    @ApiOperation("获取我的评价列表")
    @GetMapping("/my")
    public Result<PageResult<Review>> getMyReviews(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size) {
        // TODO: 实现获取我的评价列表
        return Result.success();
    }

    @ApiOperation("删除评价")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        // TODO: 实现删除评价
        return Result.success();
    }
}

