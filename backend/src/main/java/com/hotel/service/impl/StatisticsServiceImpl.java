package com.hotel.service.impl;

import com.hotel.mapper.BookingOrderMapper;
import com.hotel.mapper.HotelMapper;
import com.hotel.mapper.ReviewMapper;
import com.hotel.mapper.UserMapper;
import com.hotel.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final UserMapper userMapper;
    private final HotelMapper hotelMapper;
    private final BookingOrderMapper orderMapper;
    private final ReviewMapper reviewMapper;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalHotels", hotelMapper.selectCount(null));
        stats.put("totalOrders", orderMapper.selectCount(null));
        stats.put("totalPaidOrders", orderMapper.selectTotalPaidOrders());
        BigDecimal revenue = orderMapper.selectTotalRevenue();
        stats.put("totalRevenue", revenue != null ? revenue : BigDecimal.ZERO);
        stats.put("totalReviews", reviewMapper.selectCount(null));
        return stats;
    }

    @Override
    public Map<String, Object> getRevenueStats(String startDate) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("monthlyStats", orderMapper.selectMonthlyStats(startDate));
        return stats;
    }

    @Override
    public Map<String, Object> getHotelRanking() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("ranking", orderMapper.selectHotelRanking());
        return stats;
    }
}
