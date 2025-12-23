package com.bookmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmanager.entity.Address;

import java.util.List;

/**
 * 收货地址服务接口
 */
public interface AddressService extends IService<Address> {
    
    /**
     * 获取用户的收货地址列表
     */
    List<Address> getAddressList(Long userId);
    
    /**
     * 添加收货地址
     */
    boolean addAddress(Address address);
    
    /**
     * 更新收货地址
     */
    boolean updateAddress(Address address);
    
    /**
     * 删除收货地址
     */
    boolean deleteAddress(Long id);
    
    /**
     * 设置默认地址
     */
    boolean setDefaultAddress(Long userId, Long addressId);
    
    /**
     * 获取用户的默认地址
     */
    Address getDefaultAddress(Long userId);
}

