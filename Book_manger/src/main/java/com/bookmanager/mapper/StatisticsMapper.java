package com.bookmanager.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 数据统计Mapper接口
 */
@Mapper
public interface StatisticsMapper {

    Long countUsers();

    Long countBooks();

    Long countOrders();

    BigDecimal sumSales();

    Long countTodayUsers();

    Long countTodayOrders();

    BigDecimal sumTodaySales();

    List<Map<String, Object>> selectSalesStatistics(@Param("startDate") String startDate,
                                                    @Param("endDate") String endDate);

    List<Map<String, Object>> selectCategorySales();

    List<Map<String, Object>> selectUserGrowth(@Param("startDate") String startDate,
                                               @Param("endDate") String endDate);
}
