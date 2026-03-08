package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment")
public class Payment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String paymentNo;
    private Long orderId;
    private String orderNo;
    private Long userId;
    private BigDecimal amount;
    private Integer paymentMethod;  // 1微信 2支付宝 3银行卡
    private Integer status;         // 0待支付 1已支付 2已退款 3支付失败
    private LocalDateTime paidAt;
    private BigDecimal refundAmount;
    private LocalDateTime refundAt;
    private String refundReason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
