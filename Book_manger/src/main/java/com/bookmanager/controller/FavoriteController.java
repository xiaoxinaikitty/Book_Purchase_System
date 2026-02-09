package com.bookmanager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.entity.Favorite;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.FavoriteService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏控制器
 */
@Api(tags = "收藏管理")
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @ApiOperation("添加收藏")
    @PostMapping("/add/{bookId}")
    public Result<Void> addFavorite(@PathVariable Long bookId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        boolean success = favoriteService.addFavorite(userId, bookId);
        if (success) {
            return Result.success("收藏成功", null);
        }
        return Result.error("收藏失败");
    }

    @ApiOperation("取消收藏")
    @DeleteMapping("/remove/{bookId}")
    public Result<Void> removeFavorite(@PathVariable Long bookId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        boolean success = favoriteService.removeFavorite(userId, bookId);
        if (success) {
            return Result.success("取消收藏成功", null);
        }
        return Result.error("取消收藏失败");
    }

    @ApiOperation("获取收藏列表")
    @GetMapping("/list")
    public Result<PageResult<Favorite>> getFavoriteList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        IPage<Favorite> favoritePage = favoriteService.getFavoriteList(userId, page, size);
        PageResult<Favorite> result = PageResult.of(
                favoritePage.getTotal(),
                favoritePage.getRecords(),
                favoritePage.getCurrent(),
                favoritePage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("检查是否已收藏")
    @GetMapping("/check/{bookId}")
    public Result<Boolean> checkFavorite(@PathVariable Long bookId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return Result.success(favoriteService.isFavorite(userId, bookId));
    }
}

