package com.bookmanager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.dto.OrderDTO;
import com.bookmanager.entity.Order;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.OrderService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 订单控制器（用户端）
 */
@Api(tags = "订单管理-用户端")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping("/create")
    public Result<Order> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Order order = orderService.createOrder(userId, orderDTO.getCartIds(), orderDTO.getAddressId(), orderDTO.getRemark());
        return Result.success("创建成功", order);
    }

    @ApiOperation("获取订单列表")
    @GetMapping("/list")
    public Result<PageResult<Order>> getOrderList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("订单状态") @RequestParam(required = false) Integer status) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        IPage<Order> orderPage = orderService.getUserOrderPage(userId, page, size, status);
        PageResult<Order> result = PageResult.of(
                orderPage.getTotal(),
                orderPage.getRecords(),
                orderPage.getCurrent(),
                orderPage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/detail/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Order order = orderService.getOrderDetail(id);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException("订单不存在");
        }
        return Result.success(order);
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel/{id}")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Order order = orderService.getById(id);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException("订单不存在");
        }
        boolean success = orderService.cancelOrder(id);
        if (success) {
            return Result.success("取消成功", null);
        }
        return Result.error("取消失败");
    }

    @ApiOperation("支付订单")
    @PutMapping("/pay/{id}")
    public Result<Void> payOrder(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Order order = orderService.getById(id);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException("订单不存在");
        }
        boolean success = orderService.payOrder(id);
        if (success) {
            return Result.success("支付成功", null);
        }
        return Result.error("支付失败");
    }

    @ApiOperation("确认收货")
    @PutMapping("/confirm/{id}")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Order order = orderService.getById(id);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException("订单不存在");
        }
        boolean success = orderService.confirmOrder(id);
        if (success) {
            return Result.success("确认收货成功", null);
        }
        return Result.error("确认收货失败");
    }
}

