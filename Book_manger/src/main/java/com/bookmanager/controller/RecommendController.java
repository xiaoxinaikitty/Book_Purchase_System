package com.bookmanager.controller;

import com.bookmanager.common.Result;
import com.bookmanager.entity.Book;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.RecommendService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐控制器（KNN推荐算法）
 */
@Api(tags = "图书推荐")
@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @ApiOperation("个性化推荐（基于KNN算法）")
    @GetMapping("/personal")
    public Result<List<Book>> personalRecommend(
            @ApiParam("推荐数量") @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return Result.success(recommendService.personalRecommend(userId, 0, limit));
    }

    @ApiOperation("热门推荐")
    @GetMapping("/hot")
    public Result<List<Book>> hotRecommend(
            @ApiParam("推荐数量") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(recommendService.hotRecommend(limit));
    }

    @ApiOperation("相似图书推荐")
    @GetMapping("/similar/{bookId}")
    public Result<List<Book>> similarBookRecommend(
            @PathVariable Long bookId,
            @ApiParam("推荐数量") @RequestParam(defaultValue = "6") Integer limit) {
        return Result.success(recommendService.similarBookRecommend(bookId, limit));
    }

    @ApiOperation("猜你喜欢")
    @GetMapping("/guess")
    public Result<List<Book>> guessYouLike(
            @ApiParam("推荐数量") @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return Result.success(recommendService.guessYouLike(userId, limit));
    }

    @ApiOperation("新书推荐")
    @GetMapping("/new")
    public Result<List<Book>> newBookRecommend(
            @ApiParam("推荐数量") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(recommendService.newBookRecommend(limit));
    }
}

