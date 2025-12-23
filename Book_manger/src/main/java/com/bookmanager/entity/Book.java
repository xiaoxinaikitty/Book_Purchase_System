package com.bookmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体类
 */
@Data
@TableName("book")
public class Book implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 图书ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 书名
     */
    private String title;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 出版社
     */
    private String publisher;
    
    /**
     * ISBN号
     */
    private String isbn;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 库存
     */
    private Integer stock;
    
    /**
     * 封面图片
     */
    private String coverImage;
    
    /**
     * 图书简介
     */
    private String description;
    
    /**
     * 出版日期
     */
    private LocalDate publishDate;
    
    /**
     * 销量
     */
    private Integer sales;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 状态 0-下架 1-上架
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;
}

