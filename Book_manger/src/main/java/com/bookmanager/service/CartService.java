package com.bookmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmanager.entity.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService extends IService<Cart> {
    
    /**
     * 获取用户购物车列表
     */
    List<Cart> getCartList(Long userId);
    
    /**
     * 添加商品到购物车
     */
    boolean addToCart(Long userId, Long bookId, Integer quantity);
    
    /**
     * 更新购物车商品数量
     */
    boolean updateQuantity(Long cartId, Integer quantity);
    
    /**
     * 删除购物车商品
     */
    boolean removeFromCart(Long cartId);
    
    /**
     * 清空购物车
     */
    boolean clearCart(Long userId);
    
    /**
     * 获取购物车商品数量
     */
    Integer getCartCount(Long userId);
}

