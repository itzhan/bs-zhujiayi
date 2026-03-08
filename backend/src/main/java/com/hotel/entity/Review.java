package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long hotelId;
    private Long orderId;
    private Long roomTypeId;
    private Integer rating;
    private String content;
    private String images;          // JSON数组
    private Integer status;         // 0待审核 1已通过 2已拒绝
    private String reply;
    private LocalDateTime replyAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
