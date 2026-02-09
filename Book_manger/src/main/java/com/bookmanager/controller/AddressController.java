package com.bookmanager.controller;

import com.bookmanager.common.Result;
import com.bookmanager.dto.AddressDTO;
import com.bookmanager.entity.Address;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.AddressService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 收货地址控制器
 */
@Api(tags = "收货地址管理")
@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @ApiOperation("获取收货地址列表")
    @GetMapping("/list")
    public Result<List<Address>> getAddressList() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return Result.success(addressService.getAddressList(userId));
    }

    @ApiOperation("添加收货地址")
    @PostMapping("/add")
    public Result<Void> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(userId);
        boolean success = addressService.addAddress(address);
        if (success) {
            return Result.success("添加成功", null);
        }
        return Result.error("添加失败");
    }

    @ApiOperation("更新收货地址")
    @PutMapping("/update")
    public Result<Void> updateAddress(@Valid @RequestBody AddressDTO addressDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(userId);
        boolean success = addressService.updateAddress(address);
        if (success) {
            return Result.success("更新成功", null);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("删除收货地址")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Address address = addressService.getById(id);
        if (address == null || !userId.equals(address.getUserId())) {
            throw new BusinessException("地址不存在");
        }
        boolean success = addressService.deleteAddress(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    @ApiOperation("设置默认地址")
    @PutMapping("/default/{id}")
    public Result<Void> setDefaultAddress(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        boolean success = addressService.setDefaultAddress(userId, id);
        if (success) {
            return Result.success("设置成功", null);
        }
        return Result.error("设置失败");
    }
}

