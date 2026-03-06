package com.bookmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告已读记录实体类
 */
@Data
@TableName("notice_read")
public class NoticeRead implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long noticeId;

    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime readTime;
}
