package com.bookmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图书评价实体类
 */
@Data
@TableName("review")
public class Review implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 评价ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 图书ID
     */
    private Long bookId;
    
    /**
     * 评分 1-5分
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 用户昵称（非数据库字段）
     */
    @TableField(exist = false)
    private String nickname;
    
    /**
     * 用户头像（非数据库字段）
     */
    @TableField(exist = false)
    private String avatar;
}

