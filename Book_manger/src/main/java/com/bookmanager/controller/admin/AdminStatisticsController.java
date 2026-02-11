package com.bookmanager.controller.admin;

import com.bookmanager.common.Result;
import com.bookmanager.entity.Book;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.StatisticsService;
import com.bookmanager.utils.UserContext;
import com.bookmanager.vo.StatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据统计控制器（管理员端）
 */
@Api(tags = "数据统计-管理员端")
@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation("获取总体统计数据")
    @GetMapping("/overview")
    public Result<StatisticsVO> getOverview() {
        checkAdmin();
        return Result.success(statisticsService.getOverview());
    }

    @ApiOperation("获取销售统计（按日期）")
    @GetMapping("/sales")
    public Result<List<Map<String, Object>>> getSalesStatistics(
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        checkAdmin();
        return Result.success(statisticsService.getSalesStatistics(startDate, endDate));
    }

    @ApiOperation("获取销量排行榜")
    @GetMapping("/rank/sales")
    public Result<List<Book>> getSalesRank(
            @ApiParam("数量") @RequestParam(defaultValue = "10") Integer limit) {
        checkAdmin();
        return Result.success(statisticsService.getSalesRank(limit));
    }

    @ApiOperation("获取分类销量统计")
    @GetMapping("/category/sales")
    public Result<List<Map<String, Object>>> getCategorySales() {
        checkAdmin();
        return Result.success(statisticsService.getCategorySales());
    }

    @ApiOperation("获取用户增长统计")
    @GetMapping("/user/growth")
    public Result<List<Map<String, Object>>> getUserGrowth(
            @ApiParam("天数") @RequestParam(defaultValue = "7") Integer days) {
        checkAdmin();
        return Result.success(statisticsService.getUserGrowth(days));
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "无权限访问");
        }
    }
}

