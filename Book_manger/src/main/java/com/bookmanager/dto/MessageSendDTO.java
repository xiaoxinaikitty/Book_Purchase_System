package com.bookmanager.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 站内消息发送DTO
 */
@Data
public class MessageSendDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接收者ID（管理员群发时可为空）
     */
    private Long receiverId;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;
}
