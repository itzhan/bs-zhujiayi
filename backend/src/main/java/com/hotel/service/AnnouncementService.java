package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.common.PageResult;
import com.hotel.dto.AnnouncementDTO;
import com.hotel.entity.Announcement;

import java.util.List;

public interface AnnouncementService extends IService<Announcement> {
    List<Announcement> getPublishedAnnouncements();
    PageResult<Announcement> adminListAnnouncements(Integer page, Integer size, Integer type, Integer status);
    void createAnnouncement(AnnouncementDTO dto);
    void updateAnnouncement(Long id, AnnouncementDTO dto);
    void deleteAnnouncement(Long id);
    void publishAnnouncement(Long id);
}
