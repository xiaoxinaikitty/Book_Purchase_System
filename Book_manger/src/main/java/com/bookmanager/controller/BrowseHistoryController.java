package com.bookmanager.controller;

import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.entity.BrowseHistory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * 浏览记录控制器
 */
@Api(tags = "浏览记录")
@RestController
@RequestMapping("/api/history")
public class BrowseHistoryController {

    @ApiOperation("获取浏览记录")
    @GetMapping("/list")
    public Result<PageResult<BrowseHistory>> getBrowseHistory(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size) {
        // TODO: 实现获取浏览记录
        return Result.success();
    }

    @ApiOperation("清空浏览记录")
    @DeleteMapping("/clear")
    public Result<Void> clearHistory() {
        // TODO: 实现清空浏览记录
        return Result.success();
    }

    @ApiOperation("删除单条浏览记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteHistory(@PathVariable Long id) {
        // TODO: 实现删除单条浏览记录
        return Result.success();
    }
}

