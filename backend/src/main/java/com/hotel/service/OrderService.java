package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.common.PageResult;
import com.hotel.dto.OrderDTO;
import com.hotel.entity.BookingOrder;

public interface OrderService extends IService<BookingOrder> {
    BookingOrder createOrder(Long userId, OrderDTO dto);
    void cancelOrder(Long userId, Long orderId, String reason);
    void requestRefund(Long userId, Long orderId, String reason);
    PageResult<BookingOrder> getUserOrders(Long userId, Integer page, Integer size, Integer status);
    BookingOrder getOrderDetail(Long userId, Long orderId);
    PageResult<BookingOrder> adminListOrders(Integer page, Integer size, String orderNo, Integer status, String keyword);
    void adminUpdateOrderStatus(Long orderId, Integer status);
    void adminProcessRefund(Long orderId, boolean approved, String reason);
    void checkIn(Long orderId);
    void checkOut(Long orderId);
}
