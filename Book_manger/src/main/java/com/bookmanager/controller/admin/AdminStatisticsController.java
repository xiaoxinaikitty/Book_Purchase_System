package com.bookmanager.controller.admin;

import com.bookmanager.common.Result;
import com.bookmanager.entity.Book;
import com.bookmanager.vo.StatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @ApiOperation("获取总体统计数据")
    @GetMapping("/overview")
    public Result<StatisticsVO> getOverview() {
        // TODO: 实现获取总体统计数据
        return Result.success();
    }

    @ApiOperation("获取销售统计（按日期）")
    @GetMapping("/sales")
    public Result<List<Map<String, Object>>> getSalesStatistics(
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        // TODO: 实现获取销售统计
        return Result.success();
    }

    @ApiOperation("获取销量排行榜")
    @GetMapping("/rank/sales")
    public Result<List<Book>> getSalesRank(
            @ApiParam("数量") @RequestParam(defaultValue = "10") Integer limit) {
        // TODO: 实现获取销量排行榜
        return Result.success();
    }

    @ApiOperation("获取分类销量统计")
    @GetMapping("/category/sales")
    public Result<List<Map<String, Object>>> getCategorySales() {
        // TODO: 实现获取分类销量统计
        return Result.success();
    }

    @ApiOperation("获取用户增长统计")
    @GetMapping("/user/growth")
    public Result<List<Map<String, Object>>> getUserGrowth(
            @ApiParam("天数") @RequestParam(defaultValue = "7") Integer days) {
        // TODO: 实现获取用户增长统计
        return Result.success();
    }
}

