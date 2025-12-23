package com.bookmanager.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 收货地址DTO
 */
@Data
public class AddressDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 地址ID（更新时使用）
     */
    private Long id;
    
    /**
     * 收货人
     */
    @NotBlank(message = "收货人不能为空")
    private String receiver;
    
    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String phone;
    
    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    private String province;
    
    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    private String city;
    
    /**
     * 区县
     */
    @NotBlank(message = "区县不能为空")
    private String district;
    
    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String detail;
    
    /**
     * 是否默认地址
     */
    private Integer isDefault;
}

