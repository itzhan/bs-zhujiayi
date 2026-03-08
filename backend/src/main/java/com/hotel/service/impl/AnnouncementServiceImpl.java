package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.common.PageResult;
import com.hotel.dto.AnnouncementDTO;
import com.hotel.entity.Announcement;
import com.hotel.mapper.AnnouncementMapper;
import com.hotel.service.AnnouncementService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public List<Announcement> getPublishedAnnouncements() {
        return lambdaQuery()
                .eq(Announcement::getStatus, 1)
                .orderByDesc(Announcement::getPublishTime)
                .list();
    }

    @Override
    public PageResult<Announcement> adminListAnnouncements(Integer page, Integer size, Integer type, Integer status) {
        Page<Announcement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (type != null) wrapper.eq(Announcement::getType, type);
        if (status != null) wrapper.eq(Announcement::getStatus, status);
        wrapper.orderByDesc(Announcement::getCreatedAt);
        Page<Announcement> result = page(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public void createAnnouncement(AnnouncementDTO dto) {
        Announcement ann = new Announcement();
        ann.setTitle(dto.getTitle());
        ann.setContent(dto.getContent());
        ann.setType(dto.getType() != null ? dto.getType() : 1);
        ann.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        if (ann.getStatus() == 1) ann.setPublishTime(LocalDateTime.now());
        ann.setCreatedAt(LocalDateTime.now());
        ann.setUpdatedAt(LocalDateTime.now());
        save(ann);
    }

    @Override
    public void updateAnnouncement(Long id, AnnouncementDTO dto) {
        Announcement ann = getById(id);
        if (ann == null) throw new BusinessException(404, "公告不存在");
        ann.setTitle(dto.getTitle());
        ann.setContent(dto.getContent());
        if (dto.getType() != null) ann.setType(dto.getType());
        if (dto.getStatus() != null) ann.setStatus(dto.getStatus());
        ann.setUpdatedAt(LocalDateTime.now());
        updateById(ann);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        if (getById(id) == null) throw new BusinessException(404, "公告不存在");
        removeById(id);
    }

    @Override
    public void publishAnnouncement(Long id) {
        Announcement ann = getById(id);
        if (ann == null) throw new BusinessException(404, "公告不存在");
        ann.setStatus(1);
        ann.setPublishTime(LocalDateTime.now());
        ann.setUpdatedAt(LocalDateTime.now());
        updateById(ann);
    }
}
