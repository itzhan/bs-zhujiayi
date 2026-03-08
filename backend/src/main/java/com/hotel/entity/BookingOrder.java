package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("booking_order")
public class BookingOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long hotelId;
    private Long roomTypeId;
    private String hotelName;
    private String roomTypeName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer nights;
    private Integer roomCount;
    private String guestName;
    private String guestPhone;
    private BigDecimal totalAmount;
    private Long couponId;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private Integer status;         // 0待支付 1已支付待入住 2已入住 3已完成 4已取消 5退款中 6已退款
    private String remark;
    private String cancelReason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
