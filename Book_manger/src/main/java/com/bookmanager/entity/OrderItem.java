package com.bookmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单详情实体类
 */
@Data
@TableName("order_item")
public class OrderItem implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 图书ID
     */
    private Long bookId;
    
    /**
     * 图书标题
     */
    private String bookTitle;
    
    /**
     * 图书价格
     */
    private BigDecimal bookPrice;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 小计金额
     */
    private BigDecimal subtotal;
    
    /**
     * 图书封面（非数据库字段）
     */
    @TableField(exist = false)
    private String bookCover;
}

