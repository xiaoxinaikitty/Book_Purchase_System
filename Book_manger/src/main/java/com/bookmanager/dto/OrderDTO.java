package com.bookmanager.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 创建订单DTO
 */
@Data
public class OrderDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 购物车ID列表
     */
    @NotEmpty(message = "请选择要结算的商品")
    private List<Long> cartIds;
    
    /**
     * 收货地址ID
     */
    @NotNull(message = "请选择收货地址")
    private Long addressId;
    
    /**
     * 备注
     */
    private String remark;
}

