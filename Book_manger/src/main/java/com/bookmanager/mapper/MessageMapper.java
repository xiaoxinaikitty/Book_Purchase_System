package com.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmanager.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 站内消息 Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    IPage<Message> selectInboxPage(Page<Message> page,
                                   @Param("userId") Long userId,
                                   @Param("status") Integer status);

    IPage<Message> selectSentPage(Page<Message> page,
                                  @Param("userId") Long userId);

    IPage<Message> selectAdminSentPage(Page<Message> page,
                                       @Param("senderId") Long senderId,
                                       @Param("keyword") String keyword);

    Long countUnread(@Param("userId") Long userId);
}
