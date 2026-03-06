package com.bookmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 站内消息实体类
 */
@Data
@TableName("message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 状态 0-未读 1-已读
     */
    private Integer status;

    /**
     * 已读时间
     */
    private LocalDateTime readTime;

    /**
     * 发送方删除标记 0-未删除 1-已删除
     */
    private Integer deleteBySender;

    /**
     * 接收方删除标记 0-未删除 1-已删除
     */
    private Integer deleteByReceiver;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 发送者名称（非数据库字段）
     */
    @TableField(exist = false)
    private String senderName;

    /**
     * 接收者名称（非数据库字段）
     */
    @TableField(exist = false)
    private String receiverName;
}
