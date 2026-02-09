package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.Address;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.mapper.AddressMapper;
import com.bookmanager.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * 收货地址服务实现类
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public List<Address> getAddressList(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public boolean addAddress(Address address) {
        if (address.getUserId() == null) {
            throw new BusinessException("用户信息缺失");
        }
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }

        List<Address> existing = getAddressList(address.getUserId());
        if (existing.isEmpty()) {
            address.setIsDefault(1);
        } else if (address.getIsDefault() == 1) {
            clearDefault(address.getUserId());
        }

        return this.save(address);
    }

    @Override
    public boolean updateAddress(Address address) {
        if (address.getId() == null) {
            throw new BusinessException("地址ID不能为空");
        }
        Address exist = this.getById(address.getId());
        if (exist == null) {
            throw new BusinessException("地址不存在");
        }
        if (!exist.getUserId().equals(address.getUserId())) {
            throw new BusinessException("无权限操作该地址");
        }

        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefault(address.getUserId());
        }

        return this.updateById(address);
    }

    @Override
    public boolean deleteAddress(Long id) {
        Address exist = this.getById(id);
        if (exist == null) {
            return false;
        }
        boolean removed = this.removeById(id);
        if (removed && exist.getIsDefault() != null && exist.getIsDefault() == 1) {
            List<Address> remaining = getAddressList(exist.getUserId());
            if (!remaining.isEmpty()) {
                Address next = remaining.stream()
                        .min(Comparator.comparing(Address::getCreateTime))
                        .orElse(remaining.get(0));
                setDefaultAddress(exist.getUserId(), next.getId());
            }
        }
        return removed;
    }

    @Override
    public boolean setDefaultAddress(Long userId, Long addressId) {
        Address exist = this.getById(addressId);
        if (exist == null || !userId.equals(exist.getUserId())) {
            throw new BusinessException("地址不存在");
        }
        clearDefault(userId);
        Address update = new Address();
        update.setId(addressId);
        update.setIsDefault(1);
        return this.updateById(update);
    }

    @Override
    public Address getDefaultAddress(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId).eq(Address::getIsDefault, 1);
        Address address = this.getOne(wrapper);
        if (address != null) {
            return address;
        }
        List<Address> list = getAddressList(userId);
        return list.isEmpty() ? null : list.get(0);
    }

    private void clearDefault(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId).eq(Address::getIsDefault, 1);
        Address update = new Address();
        update.setIsDefault(0);
        this.update(update, wrapper);
    }
}

