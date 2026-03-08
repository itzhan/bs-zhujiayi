package com.hotel.service;

import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getDashboardStats();
    Map<String, Object> getRevenueStats(String startDate);
    Map<String, Object> getHotelRanking();
}
