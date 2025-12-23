package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.Category;
import com.bookmanager.mapper.CategoryMapper;
import com.bookmanager.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getAllCategories() {
        // TODO: 实现获取所有分类
        return null;
    }

    @Override
    public List<Category> getCategoryTree() {
        // TODO: 实现获取分类树结构
        return null;
    }

    @Override
    public boolean addCategory(Category category) {
        // TODO: 实现添加分类
        return false;
    }

    @Override
    public boolean updateCategory(Category category) {
        // TODO: 实现更新分类
        return false;
    }

    @Override
    public boolean deleteCategory(Long id) {
        // TODO: 实现删除分类
        return false;
    }
}

