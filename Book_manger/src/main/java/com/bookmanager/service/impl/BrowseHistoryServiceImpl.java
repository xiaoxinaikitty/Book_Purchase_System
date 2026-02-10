package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.Book;
import com.bookmanager.entity.BrowseHistory;
import com.bookmanager.mapper.BrowseHistoryMapper;
import com.bookmanager.service.BookService;
import com.bookmanager.service.BrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 浏览记录服务实现类
 */
@Service
public class BrowseHistoryServiceImpl extends ServiceImpl<BrowseHistoryMapper, BrowseHistory> implements BrowseHistoryService {

    @Autowired
    private BookService bookService;

    @Override
    public void recordBrowse(Long userId, Long bookId) {
        if (userId == null || bookId == null) {
            return;
        }
        Book book = bookService.getById(bookId);
        if (book == null || book.getStatus() == null || book.getStatus() != 1) {
            return;
        }

        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getBookId, bookId);
        BrowseHistory history = this.getOne(wrapper);
        if (history != null) {
            int count = history.getBrowseCount() == null ? 0 : history.getBrowseCount();
            history.setBrowseCount(count + 1);
            history.setLastBrowseTime(LocalDateTime.now());
            this.updateById(history);
            return;
        }

        BrowseHistory record = new BrowseHistory();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBrowseCount(1);
        record.setLastBrowseTime(LocalDateTime.now());
        this.save(record);
    }

    @Override
    public IPage<BrowseHistory> getBrowseHistory(Long userId, Integer page, Integer size) {
        Page<BrowseHistory> pageParam = new Page<>(page, size);
        return this.baseMapper.selectHistoryPage(pageParam, userId);
    }

    @Override
    public boolean clearHistory(Long userId) {
        LambdaQueryWrapper<BrowseHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowseHistory::getUserId, userId);
        return this.remove(wrapper);
    }

    @Override
    public boolean deleteHistory(Long id) {
        return this.removeById(id);
    }
}

