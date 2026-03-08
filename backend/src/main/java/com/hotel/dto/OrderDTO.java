package com.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class OrderDTO {
    @NotNull(message = "酒店ID不能为空")
    private Long hotelId;
    @NotNull(message = "房型ID不能为空")
    private Long roomTypeId;
    @NotNull(message = "入住日期不能为空")
    private LocalDate checkInDate;
    @NotNull(message = "退房日期不能为空")
    private LocalDate checkOutDate;
    private Integer roomCount = 1;
    @NotBlank(message = "入住人姓名不能为空")
    private String guestName;
    @NotBlank(message = "入住人电话不能为空")
    private String guestPhone;
    private Long couponId;
    private String remark;
}
