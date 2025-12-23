package com.bookmanager.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统计数据VO
 */
@Data
public class StatisticsVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户总数
     */
    private Long userCount;
    
    /**
     * 图书总数
     */
    private Long bookCount;
    
    /**
     * 订单总数
     */
    private Long orderCount;
    
    /**
     * 销售总额
     */
    private BigDecimal totalSales;
    
    /**
     * 今日新增用户
     */
    private Long todayUserCount;
    
    /**
     * 今日订单数
     */
    private Long todayOrderCount;
    
    /**
     * 今日销售额
     */
    private BigDecimal todaySales;
}

