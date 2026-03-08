package com.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoomTypeDTO {
    @NotNull(message = "酒店ID不能为空")
    private Long hotelId;
    @NotBlank(message = "房型名称不能为空")
    private String name;
    private String description;
    @NotNull(message = "价格不能为空")
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer capacity;
    private String bedType;
    private BigDecimal area;
    private String floor;
    private String facilities;
    private String images;
    private Integer breakfast;
    private Integer hasWindow;
    private Integer totalRooms;
    private Integer status;
}
