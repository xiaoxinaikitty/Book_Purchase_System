package com.bookmanager.controller;

import com.bookmanager.common.Result;
import com.bookmanager.dto.CartDTO;
import com.bookmanager.entity.Cart;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.CartService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 购物车控制器
 */
@Api(tags = "购物车管理")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @ApiOperation("获取购物车列表")
    @GetMapping("/list")
    public Result<List<Cart>> getCartList() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return Result.success(cartService.getCartList(userId));
    }

    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public Result<Void> addToCart(@Valid @RequestBody CartDTO cartDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        boolean success = cartService.addToCart(userId, cartDTO.getBookId(), cartDTO.getQuantity());
        if (success) {
            return Result.success("添加成功", null);
        }
        return Result.error("添加失败");
    }

    @ApiOperation("更新购物车商品数量")
    @PutMapping("/update")
    public Result<Void> updateQuantity(
            @ApiParam("购物车ID") @RequestParam Long cartId,
            @ApiParam("数量") @RequestParam Integer quantity) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Cart cart = cartService.getById(cartId);
        if (cart == null || !userId.equals(cart.getUserId())) {
            throw new BusinessException("购物车记录不存在");
        }
        boolean success = cartService.updateQuantity(cartId, quantity);
        if (success) {
            return Result.success("更新成功", null);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("删除购物车商品")
    @DeleteMapping("/{id}")
    public Result<Void> removeFromCart(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Cart cart = cartService.getById(id);
        if (cart == null || !userId.equals(cart.getUserId())) {
            throw new BusinessException("购物车记录不存在");
        }
        boolean success = cartService.removeFromCart(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    @ApiOperation("清空购物车")
    @DeleteMapping("/clear")
    public Result<Void> clearCart() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        boolean success = cartService.clearCart(userId);
        if (success) {
            return Result.success("清空成功", null);
        }
        return Result.error("清空失败");
    }

    @ApiOperation("获取购物车商品数量")
    @GetMapping("/count")
    public Result<Integer> getCartCount() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return Result.success(cartService.getCartCount(userId));
    }
}

