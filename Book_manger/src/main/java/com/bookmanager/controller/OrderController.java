package com.bookmanager.controller;

import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.dto.OrderDTO;
import com.bookmanager.entity.Order;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 订单控制器（用户端）
 */
@Api(tags = "订单管理-用户端")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @ApiOperation("创建订单")
    @PostMapping("/create")
    public Result<Order> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        // TODO: 实现创建订单
        return Result.success();
    }

    @ApiOperation("获取订单列表")
    @GetMapping("/list")
    public Result<PageResult<Order>> getOrderList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("订单状态") @RequestParam(required = false) Integer status) {
        // TODO: 实现获取订单列表
        return Result.success();
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/detail/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        // TODO: 实现获取订单详情
        return Result.success();
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel/{id}")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        // TODO: 实现取消订单
        return Result.success();
    }

    @ApiOperation("支付订单")
    @PutMapping("/pay/{id}")
    public Result<Void> payOrder(@PathVariable Long id) {
        // TODO: 实现支付订单
        return Result.success();
    }

    @ApiOperation("确认收货")
    @PutMapping("/confirm/{id}")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        // TODO: 实现确认收货
        return Result.success();
    }
}

