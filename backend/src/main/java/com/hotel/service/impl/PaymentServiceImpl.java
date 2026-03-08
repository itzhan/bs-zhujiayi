package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.common.PageResult;
import com.hotel.dto.PaymentDTO;
import com.hotel.entity.BookingOrder;
import com.hotel.entity.Payment;
import com.hotel.mapper.PaymentMapper;
import com.hotel.service.OrderService;
import com.hotel.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @Lazy
    private final OrderService orderService;

    @Override
    @Transactional
    public Payment pay(Long userId, PaymentDTO dto) {
        BookingOrder order = orderService.getById(dto.getOrderId());
        if (order == null) throw new BusinessException(404, "订单不存在");
        if (!order.getUserId().equals(userId)) throw new BusinessException(403, "无权操作此订单");
        if (order.getStatus() != 0) throw new BusinessException("订单状态不允许支付");

        // 模拟支付成功
        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(order.getId());
        payment.setOrderNo(order.getOrderNo());
        payment.setUserId(userId);
        payment.setAmount(order.getActualAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(1); // 已支付
        payment.setPaidAt(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        save(payment);

        // 更新订单状态
        order.setStatus(1); // 已支付待入住
        order.setUpdatedAt(LocalDateTime.now());
        orderService.updateById(order);

        return payment;
    }

    @Override
    public Payment getByOrderId(Long orderId) {
        return lambdaQuery().eq(Payment::getOrderId, orderId).one();
    }

    @Override
    public PageResult<Payment> adminListPayments(Integer page, Integer size, String paymentNo, Integer status) {
        Page<Payment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(paymentNo)) wrapper.like(Payment::getPaymentNo, paymentNo);
        if (status != null) wrapper.eq(Payment::getStatus, status);
        wrapper.orderByDesc(Payment::getCreatedAt);
        Page<Payment> result = page(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional
    public void refund(Long orderId, String reason) {
        Payment payment = getByOrderId(orderId);
        if (payment == null) throw new BusinessException(404, "支付记录不存在");
        payment.setStatus(2); // 已退款
        payment.setRefundAmount(payment.getAmount());
        payment.setRefundAt(LocalDateTime.now());
        payment.setRefundReason(reason);
        payment.setUpdatedAt(LocalDateTime.now());
        updateById(payment);
    }

    private String generatePaymentNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "PAY" + date + random;
    }
}
