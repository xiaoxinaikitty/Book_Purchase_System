package com.bookmanager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.entity.BrowseHistory;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.BrowseHistoryService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 浏览记录控制器
 */
@Api(tags = "浏览记录")
@RestController
@RequestMapping("/api/history")
public class BrowseHistoryController {

    @Autowired
    private BrowseHistoryService browseHistoryService;

    @ApiOperation("获取浏览记录")
    @GetMapping("/list")
    public Result<PageResult<BrowseHistory>> getBrowseHistory(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        IPage<BrowseHistory> historyPage = browseHistoryService.getBrowseHistory(userId, page, size);
        PageResult<BrowseHistory> result = PageResult.of(
                historyPage.getTotal(),
                historyPage.getRecords(),
                historyPage.getCurrent(),
                historyPage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("清空浏览记录")
    @DeleteMapping("/clear")
    public Result<Void> clearHistory() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        boolean success = browseHistoryService.clearHistory(userId);
        if (success) {
            return Result.success("清空成功", null);
        }
        return Result.error("清空失败");
    }

    @ApiOperation("删除单条浏览记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteHistory(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        BrowseHistory history = browseHistoryService.getById(id);
        if (history == null || !userId.equals(history.getUserId())) {
            throw new BusinessException("浏览记录不存在");
        }
        boolean success = browseHistoryService.deleteHistory(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }
}

