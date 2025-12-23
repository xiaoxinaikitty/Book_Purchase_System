package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.Address;
import com.bookmanager.mapper.AddressMapper;
import com.bookmanager.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收货地址服务实现类
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public List<Address> getAddressList(Long userId) {
        // TODO: 实现获取用户收货地址列表
        return null;
    }

    @Override
    public boolean addAddress(Address address) {
        // TODO: 实现添加收货地址
        return false;
    }

    @Override
    public boolean updateAddress(Address address) {
        // TODO: 实现更新收货地址
        return false;
    }

    @Override
    public boolean deleteAddress(Long id) {
        // TODO: 实现删除收货地址
        return false;
    }

    @Override
    public boolean setDefaultAddress(Long userId, Long addressId) {
        // TODO: 实现设置默认地址
        return false;
    }

    @Override
    public Address getDefaultAddress(Long userId) {
        // TODO: 实现获取用户默认地址
        return null;
    }
}

