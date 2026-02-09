package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.Book;
import com.bookmanager.entity.Favorite;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.mapper.FavoriteMapper;
import com.bookmanager.service.BookService;
import com.bookmanager.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 收藏服务实现类
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private BookService bookService;

    @Override
    public boolean addFavorite(Long userId, Long bookId) {
        if (userId == null || bookId == null) {
            throw new BusinessException("收藏参数不完整");
        }
        Book book = bookService.getById(bookId);
        if (book == null || book.getStatus() == null || book.getStatus() != 1) {
            throw new BusinessException("图书不存在或已下架");
        }

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getBookId, bookId);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("已收藏该图书");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        return this.save(favorite);
    }

    @Override
    public boolean removeFavorite(Long userId, Long bookId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getBookId, bookId);
        Favorite favorite = this.getOne(wrapper);
        if (favorite == null) {
            throw new BusinessException("收藏记录不存在");
        }
        return this.removeById(favorite.getId());
    }

    @Override
    public IPage<Favorite> getFavoriteList(Long userId, Integer page, Integer size) {
        Page<Favorite> pageParam = new Page<>(page, size);
        return this.baseMapper.selectFavoritePage(pageParam, userId);
    }

    @Override
    public boolean isFavorite(Long userId, Long bookId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getBookId, bookId);
        return this.count(wrapper) > 0;
    }

    @Override
    public Integer getFavoriteCount(Long userId) {
        return Math.toIntExact(this.lambdaQuery().eq(Favorite::getUserId, userId).count());
    }
}

