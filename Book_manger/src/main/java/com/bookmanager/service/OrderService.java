package com.bookmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmanager.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单
     */
    Order createOrder(Long userId, List<Long> cartIds, Long addressId, String remark);
    
    /**
     * 获取用户订单分页
     */
    IPage<Order> getUserOrderPage(Long userId, Integer page, Integer size, Integer status);
    
    /**
     * 获取订单详情
     */
    Order getOrderDetail(Long orderId);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId);
    
    /**
     * 支付订单
     */
    boolean payOrder(Long orderId);
    
    /**
     * 管理员获取所有订单
     */
    IPage<Order> getAllOrderPage(Integer page, Integer size, Integer status, String orderNo);
    
    /**
     * 订单发货
     */
    boolean shipOrder(Long orderId);
    
    /**
     * 确认收货
     */
    boolean confirmOrder(Long orderId);
}

