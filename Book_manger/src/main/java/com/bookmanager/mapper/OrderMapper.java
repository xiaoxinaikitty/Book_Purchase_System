package com.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmanager.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 分页查询用户订单
     */
    IPage<Order> selectOrderPage(Page<Order> page, @Param("userId") Long userId, @Param("status") Integer status);
    
    /**
     * 管理员分页查询所有订单
     */
    IPage<Order> selectAllOrderPage(Page<Order> page, @Param("status") Integer status, @Param("orderNo") String orderNo);
    
    /**
     * 根据ID查询订单详情
     */
    Order selectOrderById(@Param("id") Long id);
}

