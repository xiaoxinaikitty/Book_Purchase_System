package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.Book;
import com.bookmanager.entity.Cart;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.mapper.CartMapper;
import com.bookmanager.service.BookService;
import com.bookmanager.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private BookService bookService;

    @Override
    public List<Cart> getCartList(Long userId) {
        return this.baseMapper.selectCartListByUserId(userId);
    }

    @Override
    public boolean addToCart(Long userId, Long bookId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("数量至少为1");
        }
        Book book = bookService.getById(bookId);
        if (book == null || book.getStatus() == null || book.getStatus() != 1) {
            throw new BusinessException("图书不存在或已下架");
        }
        int stock = book.getStock() == null ? 0 : book.getStock();
        if (stock < quantity) {
            throw new BusinessException("库存不足");
        }

        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId).eq(Cart::getBookId, bookId);
        Cart exist = this.getOne(wrapper);
        if (exist != null) {
            int newQty = exist.getQuantity() + quantity;
            if (newQty > stock) {
                throw new BusinessException("库存不足");
            }
            exist.setQuantity(newQty);
            return this.updateById(exist);
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setBookId(bookId);
        cart.setQuantity(quantity);
        return this.save(cart);
    }

    @Override
    public boolean updateQuantity(Long cartId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("数量至少为1");
        }
        Cart cart = this.getById(cartId);
        if (cart == null) {
            throw new BusinessException("购物车记录不存在");
        }
        Book book = bookService.getById(cart.getBookId());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        int stock = book.getStock() == null ? 0 : book.getStock();
        if (quantity > stock) {
            throw new BusinessException("库存不足");
        }
        cart.setQuantity(quantity);
        return this.updateById(cart);
    }

    @Override
    public boolean removeFromCart(Long cartId) {
        return this.removeById(cartId);
    }

    @Override
    public boolean clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        return this.remove(wrapper);
    }

    @Override
    public Integer getCartCount(Long userId) {
        List<Cart> list = this.lambdaQuery().eq(Cart::getUserId, userId).list();
        int total = 0;
        for (Cart cart : list) {
            if (cart.getQuantity() != null) {
                total += cart.getQuantity();
            }
        }
        return total;
    }
}

