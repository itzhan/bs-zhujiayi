package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hotel")
public class Hotel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String province;
    private String phone;
    private Integer starRating;
    private String coverImage;
    private String images;          // JSON数组
    private String facilities;      // JSON数组
    private String checkInTime;
    private String checkOutTime;
    private BigDecimal minPrice;
    private Integer status;         // 0下架 1上架
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
