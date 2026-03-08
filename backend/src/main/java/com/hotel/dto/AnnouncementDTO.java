package com.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnnouncementDTO {
    @NotBlank(message = "公告标题不能为空")
    private String title;
    @NotBlank(message = "公告内容不能为空")
    private String content;
    private Integer type;   // 1通知 2活动 3公告
    private Integer status; // 0草稿 1已发布 2已下架
}
