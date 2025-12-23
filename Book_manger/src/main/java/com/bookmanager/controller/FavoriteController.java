package com.bookmanager.controller;

import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.entity.Favorite;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏控制器
 */
@Api(tags = "收藏管理")
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @ApiOperation("添加收藏")
    @PostMapping("/add/{bookId}")
    public Result<Void> addFavorite(@PathVariable Long bookId) {
        // TODO: 实现添加收藏
        return Result.success();
    }

    @ApiOperation("取消收藏")
    @DeleteMapping("/remove/{bookId}")
    public Result<Void> removeFavorite(@PathVariable Long bookId) {
        // TODO: 实现取消收藏
        return Result.success();
    }

    @ApiOperation("获取收藏列表")
    @GetMapping("/list")
    public Result<PageResult<Favorite>> getFavoriteList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size) {
        // TODO: 实现获取收藏列表
        return Result.success();
    }

    @ApiOperation("检查是否已收藏")
    @GetMapping("/check/{bookId}")
    public Result<Boolean> checkFavorite(@PathVariable Long bookId) {
        // TODO: 实现检查是否已收藏
        return Result.success();
    }
}

