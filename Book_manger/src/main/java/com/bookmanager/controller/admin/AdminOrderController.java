package com.bookmanager.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.entity.Order;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.OrderService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理控制器（管理员端）
 */
@Api(tags = "订单管理-管理员端")
@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("获取订单列表（分页）")
    @GetMapping("/list")
    public Result<PageResult<Order>> getOrderList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("订单状态") @RequestParam(required = false) Integer status,
            @ApiParam("订单号") @RequestParam(required = false) String orderNo) {
        checkAdmin();
        IPage<Order> orderPage = orderService.getAllOrderPage(page, size, status, orderNo);
        PageResult<Order> result = PageResult.of(
                orderPage.getTotal(),
                orderPage.getRecords(),
                orderPage.getCurrent(),
                orderPage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        checkAdmin();
        Order order = orderService.getOrderDetail(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return Result.success(order);
    }

    @ApiOperation("订单发货")
    @PutMapping("/ship/{id}")
    public Result<Void> shipOrder(@PathVariable Long id) {
        checkAdmin();
        boolean success = orderService.shipOrder(id);
        if (success) {
            return Result.success("发货成功", null);
        }
        return Result.error("发货失败");
    }

    @ApiOperation("更新订单状态")
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @ApiParam("订单状态") @RequestParam Integer status) {
        checkAdmin();
        Order order = orderService.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        Order update = new Order();
        update.setId(id);
        update.setStatus(status);
        boolean success = orderService.updateById(update);
        if (success) {
            return Result.success("状态更新成功", null);
        }
        return Result.error("状态更新失败");
    }

    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        checkAdmin();
        boolean success = orderService.removeById(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "无权限访问");
        }
    }
}

