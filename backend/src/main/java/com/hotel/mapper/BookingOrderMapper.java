package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.BookingOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface BookingOrderMapper extends BaseMapper<BookingOrder> {

    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m') as month, COUNT(*) as count, SUM(actual_amount) as amount " +
            "FROM booking_order WHERE status IN (1,2,3) AND created_at >= #{startDate} " +
            "GROUP BY DATE_FORMAT(created_at, '%Y-%m') ORDER BY month")
    List<Map<String, Object>> selectMonthlyStats(@Param("startDate") String startDate);

    @Select("SELECT h.name as hotelName, COUNT(*) as orderCount, SUM(bo.actual_amount) as totalAmount " +
            "FROM booking_order bo JOIN hotel h ON bo.hotel_id = h.id " +
            "WHERE bo.status IN (1,2,3) GROUP BY bo.hotel_id ORDER BY totalAmount DESC LIMIT 10")
    List<Map<String, Object>> selectHotelRanking();

    @Select("SELECT SUM(actual_amount) FROM booking_order WHERE status IN (1,2,3)")
    BigDecimal selectTotalRevenue();

    @Select("SELECT COUNT(*) FROM booking_order WHERE status IN (1,2,3)")
    Integer selectTotalPaidOrders();
}
