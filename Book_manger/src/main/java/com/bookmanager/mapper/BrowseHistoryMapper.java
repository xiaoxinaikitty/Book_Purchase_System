package com.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmanager.entity.BrowseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 浏览记录Mapper接口
 */
@Mapper
public interface BrowseHistoryMapper extends BaseMapper<BrowseHistory> {
    
    /**
     * 查询用户浏览记录（包含图书信息）
     */
    List<BrowseHistory> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 分页查询用户浏览记录（包含图书信息）
     */
    IPage<BrowseHistory> selectHistoryPage(Page<BrowseHistory> page, @Param("userId") Long userId);
}

