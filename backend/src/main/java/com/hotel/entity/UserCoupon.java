package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_coupon")
public class UserCoupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long couponId;
    private Long orderId;
    private Integer status;         // 0未使用 1已使用 2已过期
    private LocalDateTime receivedAt;
    private LocalDateTime usedAt;
}
