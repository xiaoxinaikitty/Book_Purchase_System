package com.bookmanager.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookmanager.common.PageResult;
import com.bookmanager.common.Result;
import com.bookmanager.entity.Book;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.BookService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 图书管理控制器（管理员端）
 */
@Api(tags = "图书管理-管理员端")
@RestController
@RequestMapping("/api/admin/book")
public class AdminBookController {

    @Autowired
    private BookService bookService;

    @ApiOperation("获取图书列表（分页）")
    @GetMapping("/list")
    public Result<PageResult<Book>> getBookList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        checkAdmin();
        IPage<Book> bookPage = bookService.getBookPageAdmin(page, size, categoryId, keyword, status);
        PageResult<Book> result = PageResult.of(
                bookPage.getTotal(),
                bookPage.getRecords(),
                bookPage.getCurrent(),
                bookPage.getSize()
        );
        return Result.success(result);
    }

    @ApiOperation("获取图书详情")
    @GetMapping("/{id}")
    public Result<Book> getBookDetail(@PathVariable Long id) {
        checkAdmin();
        Book book = bookService.getBookDetail(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        return Result.success(book);
    }

    @ApiOperation("添加图书")
    @PostMapping("/add")
    public Result<Void> addBook(@Valid @RequestBody Book book) {
        checkAdmin();
        boolean success = bookService.addBook(book);
        if (success) {
            return Result.success("添加成功", null);
        }
        return Result.error("添加失败");
    }

    @ApiOperation("更新图书")
    @PutMapping("/update")
    public Result<Void> updateBook(@Valid @RequestBody Book book) {
        checkAdmin();
        boolean success = bookService.updateBook(book);
        if (success) {
            return Result.success("更新成功", null);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("删除图书")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable Long id) {
        checkAdmin();
        boolean success = bookService.deleteBook(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    @ApiOperation("上架/下架图书")
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @ApiParam("状态 0-下架 1-上架") @RequestParam Integer status) {
        checkAdmin();
        boolean success = bookService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功", null);
        }
        return Result.error("状态更新失败");
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "无权限访问");
        }
    }
}

