package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.Announcement;
import com.hotel.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/published")
    public Result<List<Announcement>> getPublishedAnnouncements() {
        return Result.success(announcementService.getPublishedAnnouncements());
    }
}
