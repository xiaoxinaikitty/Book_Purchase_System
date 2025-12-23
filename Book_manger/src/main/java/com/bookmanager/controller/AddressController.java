package com.bookmanager.controller;

import com.bookmanager.common.Result;
import com.bookmanager.dto.AddressDTO;
import com.bookmanager.entity.Address;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation("获取收货地址列表")
    @GetMapping("/list")
    public Result<List<Address>> getAddressList() {
        // TODO: 实现获取地址列表
        return Result.success();
    }

    @ApiOperation("添加收货地址")
    @PostMapping("/add")
    public Result<Void> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        // TODO: 实现添加地址
        return Result.success();
    }

    @ApiOperation("更新收货地址")
    @PutMapping("/update")
    public Result<Void> updateAddress(@Valid @RequestBody AddressDTO addressDTO) {
        // TODO: 实现更新地址
        return Result.success();
    }

    @ApiOperation("删除收货地址")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        // TODO: 实现删除地址
        return Result.success();
    }

    @ApiOperation("设置默认地址")
    @PutMapping("/default/{id}")
    public Result<Void> setDefaultAddress(@PathVariable Long id) {
        // TODO: 实现设置默认地址
        return Result.success();
    }
}

