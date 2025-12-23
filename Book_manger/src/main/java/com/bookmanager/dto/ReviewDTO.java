package com.bookmanager.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 图书评价DTO
 */
@Data
public class ReviewDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 图书ID
     */
    @NotNull(message = "图书ID不能为空")
    private Long bookId;
    
    /**
     * 评分 1-5分
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1分")
    @Max(value = 5, message = "评分最高为5分")
    private Integer rating;
    
    /**
     * 评价内容
     */
    @NotBlank(message = "评价内容不能为空")
    private String content;
}

