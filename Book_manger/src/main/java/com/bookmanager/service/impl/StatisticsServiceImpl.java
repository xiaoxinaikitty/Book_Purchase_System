package com.bookmanager.service.impl;

import com.bookmanager.entity.Book;
import com.bookmanager.mapper.StatisticsMapper;
import com.bookmanager.service.BookService;
import com.bookmanager.service.StatisticsService;
import com.bookmanager.vo.StatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 数据统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Autowired
    private BookService bookService;

    @Override
    public StatisticsVO getOverview() {
        StatisticsVO vo = new StatisticsVO();
        vo.setUserCount(nvl(statisticsMapper.countUsers()));
        vo.setBookCount(nvl(statisticsMapper.countBooks()));
        vo.setOrderCount(nvl(statisticsMapper.countOrders()));
        vo.setTotalSales(nvl(statisticsMapper.sumSales()));
        vo.setTodayUserCount(nvl(statisticsMapper.countTodayUsers()));
        vo.setTodayOrderCount(nvl(statisticsMapper.countTodayOrders()));
        vo.setTodaySales(nvl(statisticsMapper.sumTodaySales()));
        return vo;
    }

    @Override
    public List<Map<String, Object>> getSalesStatistics(String startDate, String endDate) {
        LocalDate end = parseDateOrDefault(endDate, LocalDate.now());
        LocalDate start = parseDateOrDefault(startDate, end.minusDays(6));
        if (start.isAfter(end)) {
            LocalDate tmp = start;
            start = end;
            end = tmp;
        }
        List<Map<String, Object>> raw = statisticsMapper.selectSalesStatistics(
                start.format(DATE_FORMAT),
                end.format(DATE_FORMAT)
        );
        Map<String, Map<String, Object>> map = toDateMap(raw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String key = date.format(DATE_FORMAT);
            Map<String, Object> item = map.get(key);
            if (item == null) {
                item = new HashMap<>();
                item.put("date", key);
                item.put("totalSales", BigDecimal.ZERO);
                item.put("orderCount", 0L);
            } else {
                item.put("date", key);
                if (item.get("totalSales") == null) {
                    item.put("totalSales", BigDecimal.ZERO);
                }
                if (item.get("orderCount") == null) {
                    item.put("orderCount", 0L);
                }
            }
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Book> getSalesRank(Integer limit) {
        return bookService.getHotBooks(limit);
    }

    @Override
    public List<Map<String, Object>> getCategorySales() {
        return statisticsMapper.selectCategorySales();
    }

    @Override
    public List<Map<String, Object>> getUserGrowth(Integer days) {
        int dayCount = days == null || days <= 0 ? 7 : days;
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(dayCount - 1);
        List<Map<String, Object>> raw = statisticsMapper.selectUserGrowth(
                start.format(DATE_FORMAT),
                end.format(DATE_FORMAT)
        );
        Map<String, Map<String, Object>> map = toDateMap(raw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String key = date.format(DATE_FORMAT);
            Map<String, Object> item = map.get(key);
            if (item == null) {
                item = new HashMap<>();
                item.put("date", key);
                item.put("userCount", 0L);
            } else {
                item.put("date", key);
                if (item.get("userCount") == null) {
                    item.put("userCount", 0L);
                }
            }
            result.add(item);
        }
        return result;
    }

    private LocalDate parseDateOrDefault(String date, LocalDate defaultDate) {
        if (date == null || date.trim().isEmpty()) {
            return defaultDate;
        }
        try {
            return LocalDate.parse(date, DATE_FORMAT);
        } catch (Exception ex) {
            return defaultDate;
        }
    }

    private Map<String, Map<String, Object>> toDateMap(List<Map<String, Object>> rows) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Object dateObj = row.get("date");
            if (dateObj == null) {
                continue;
            }
            String key = String.valueOf(dateObj);
            map.put(key, row);
        }
        return map;
    }

    private Long nvl(Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
