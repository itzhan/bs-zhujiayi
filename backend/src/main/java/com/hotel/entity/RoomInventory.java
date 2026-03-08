package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("room_inventory")
public class RoomInventory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roomTypeId;
    private LocalDate date;
    private Integer totalCount;
    private Integer bookedCount;
    private BigDecimal price;
    private Integer status;         // 0不可订 1可订
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
