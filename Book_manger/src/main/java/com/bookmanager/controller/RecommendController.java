package com.bookmanager.controller;

import com.bookmanager.common.Result;
import com.bookmanager.entity.Book;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐控制器（KNN推荐算法）
 */
@Api(tags = "图书推荐")
@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @ApiOperation("个性化推荐（基于KNN算法）")
    @GetMapping("/personal")
    public Result<List<Book>> personalRecommend(
            @ApiParam("推荐数量") @RequestParam(defaultValue = "10") Integer limit) {
        // TODO: 实现个性化推荐
        return Result.success();
    }

    @ApiOperation("热门推荐")
    @GetMapping("/hot")
    public Result<List<Book>> hotRecommend(
            @ApiParam("推荐数量") @RequestParam(defaultValue = "10") Integer limit) {
        // TODO: 实现热门推荐
        return Result.success();
    }

    @ApiOperation("相似图书推荐")
    @GetMapping("/similar/{bookId}")
    public Result<List<Book>> similarBookRecommend(
            @PathVariable Long bookId,
            @ApiParam("推荐数量") @RequestParam(defaultValue = "6") Integer limit) {
        // TODO: 实现相似图书推荐
        return Result.success();
    }

    @ApiOperation("猜你喜欢")
    @GetMapping("/guess")
    public Result<List<Book>> guessYouLike(
            @ApiParam("推荐数量") @RequestParam(defaultValue = "10") Integer limit) {
        // TODO: 实现猜你喜欢
        return Result.success();
    }

    @ApiOperation("新书推荐")
    @GetMapping("/new")
    public Result<List<Book>> newBookRecommend(
            @ApiParam("推荐数量") @RequestParam(defaultValue = "10") Integer limit) {
        // TODO: 实现新书推荐
        return Result.success();
    }
}

