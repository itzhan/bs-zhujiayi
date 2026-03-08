package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("room_type")
public class RoomType {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long hotelId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer capacity;
    private String bedType;
    private BigDecimal area;
    private String floor;
    private String facilities;      // JSON数组
    private String images;          // JSON数组
    private Integer breakfast;      // 0否 1是
    private Integer hasWindow;      // 0无 1有
    private Integer totalRooms;
    private Integer status;         // 0下架 1上架
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
