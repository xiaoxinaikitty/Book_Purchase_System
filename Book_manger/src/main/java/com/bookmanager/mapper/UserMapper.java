package com.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookmanager.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 获取指定角色用户ID列表
     */
    @Select("SELECT id FROM user WHERE role = #{role}")
    List<Long> selectUserIdsByRole(@Param("role") Integer role);
}

