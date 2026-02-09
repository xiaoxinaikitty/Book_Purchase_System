package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.Category;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.mapper.CategoryMapper;
import com.bookmanager.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getAllCategories() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort, Category::getId);
        return this.list(wrapper);
    }

    @Override
    public List<Category> getCategoryTree() {
        List<Category> all = getAllCategories();
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, Category> map = all.stream()
                .collect(Collectors.toMap(Category::getId, item -> item, (a, b) -> a));
        List<Category> roots = new ArrayList<>();
        for (Category category : all) {
            Long parentId = category.getParentId();
            if (parentId == null || parentId == 0L) {
                roots.add(category);
            } else {
                Category parent = map.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(category);
                } else {
                    roots.add(category);
                }
            }
        }
        return roots;
    }

    @Override
    public boolean addCategory(Category category) {
        return this.save(category);
    }

    @Override
    public boolean updateCategory(Category category) {
        if (category.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        return this.updateById(category);
    }

    @Override
    public boolean deleteCategory(Long id) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, id);
        long childCount = this.count(wrapper);
        if (childCount > 0) {
            throw new BusinessException("请先删除子分类");
        }
        return this.removeById(id);
    }
}

