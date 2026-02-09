package com.bookmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 图书分类实体类
 */
@Data
@TableName("category")
public class Category implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 分类ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 父分类ID，0表示一级分类
     */
    private Long parentId;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 子分类列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Category> children;
}

