package com.bookmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户浏览记录实体类（用于KNN推荐）
 */
@Data
@TableName("browse_history")
public class BrowseHistory implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * ID
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
     * 浏览次数
     */
    private Integer browseCount;
    
    /**
     * 最后浏览时间
     */
    private LocalDateTime lastBrowseTime;
    
    /**
     * 图书信息（非数据库字段）
     */
    @TableField(exist = false)
    private Book book;
}

