package com.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class HotelDTO {
    @NotBlank(message = "酒店名称不能为空")
    private String name;
    private String description;
    @NotBlank(message = "详细地址不能为空")
    private String address;
    @NotBlank(message = "城市不能为空")
    private String city;
    private String province;
    private String phone;
    private Integer starRating;
    private String coverImage;
    private String images;
    private String facilities;
    private String checkInTime;
    private String checkOutTime;
    private BigDecimal minPrice;
    private Integer status;
}
