package com.bookmanager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.entity.Book;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.BookService;
import com.bookmanager.service.BrowseHistoryService;
import com.bookmanager.utils.JwtUtils;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * 图书控制器（用户端）
 */
@Api(tags = "图书管理-用户端")
@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired(required = false)
    private BrowseHistoryService browseHistoryService;

    @Autowired
    private JwtUtils jwtUtils;

    @ApiOperation("获取图书列表（分页）")
    @GetMapping("/list")
    public Result<PageResult<Book>> getBookList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        IPage<Book> bookPage = bookService.getBookPage(page, size, categoryId, keyword);
        PageResult<Book> result = PageResult.of(
                bookPage.getTotal(),
                bookPage.getRecords(),
                bookPage.getCurrent(),
                bookPage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("获取图书详情")
    @GetMapping("/detail/{id}")
    public Result<Book> getBookDetail(@PathVariable Long id, HttpServletRequest request) {
        Book book = bookService.getBookDetail(id);
        if (book == null || book.getStatus() == null || book.getStatus() != 1) {
            throw new BusinessException("图书不存在或已下架");
        }

        Long userId = UserContext.getUserId();
        if (userId == null) {
            userId = getUserIdFromRequest(request);
        }
        if (userId != null && browseHistoryService != null) {
            browseHistoryService.recordBrowse(userId, id);
        }

        return Result.success(book);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || header.isEmpty()) {
            return null;
        }
        String token = header.startsWith("Bearer ") ? header.substring(7) : header;
        if (!jwtUtils.validateToken(token)) {
            return null;
        }
        return jwtUtils.getUserIdFromToken(token);
    }

    @ApiOperation("搜索图书")
    @GetMapping("/search")
    public Result<PageResult<Book>> searchBooks(
            @ApiParam("关键词") @RequestParam String keyword,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Book> bookPage = bookService.getBookPage(page, size, null, keyword);
        PageResult<Book> result = PageResult.of(
                bookPage.getTotal(),
                bookPage.getRecords(),
                bookPage.getCurrent(),
                bookPage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("获取热门图书")
    @GetMapping("/hot")
    public Result<List<Book>> getHotBooks(
            @ApiParam("数量") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(bookService.getHotBooks(limit));
    }

    @ApiOperation("获取新书推荐")
    @GetMapping("/new")
    public Result<List<Book>> getNewBooks(
            @ApiParam("数量") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(bookService.getNewBooks(limit));
    }
}

