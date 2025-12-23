package com.bookmanager.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 购物车操作DTO
 */
@Data
public class CartDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 图书ID
     */
    @NotNull(message = "图书ID不能为空")
    private Long bookId;
    
    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为1")
    private Integer quantity;
}

