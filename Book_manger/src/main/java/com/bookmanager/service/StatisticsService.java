package com.bookmanager.service;

import com.bookmanager.entity.Book;
import com.bookmanager.vo.StatisticsVO;

import java.util.List;
import java.util.Map;

/**
 * 数据统计服务接口
 */
public interface StatisticsService {

    StatisticsVO getOverview();

    List<Map<String, Object>> getSalesStatistics(String startDate, String endDate);

    List<Book> getSalesRank(Integer limit);

    List<Map<String, Object>> getCategorySales();

    List<Map<String, Object>> getUserGrowth(Integer days);
}
