package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.common.PageResult;
import com.hotel.dto.PaymentDTO;
import com.hotel.entity.Payment;

public interface PaymentService extends IService<Payment> {
    Payment pay(Long userId, PaymentDTO dto);
    Payment getByOrderId(Long orderId);
    PageResult<Payment> adminListPayments(Integer page, Integer size, String paymentNo, Integer status);
    void refund(Long orderId, String reason);
}
