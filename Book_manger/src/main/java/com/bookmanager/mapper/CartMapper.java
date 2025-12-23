package com.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookmanager.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车Mapper接口
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {
    
    /**
     * 查询用户购物车列表（包含图书信息）
     */
    List<Cart> selectCartListByUserId(@Param("userId") Long userId);
}

