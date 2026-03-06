package com.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmanager.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 公告 Mapper
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    IPage<Notice> selectAdminNoticePage(Page<Notice> page,
                                        @Param("keyword") String keyword,
                                        @Param("status") Integer status);

    IPage<Notice> selectUserNoticePage(Page<Notice> page,
                                       @Param("userId") Long userId,
                                       @Param("keyword") String keyword);

    Long countUnread(@Param("userId") Long userId);
}
