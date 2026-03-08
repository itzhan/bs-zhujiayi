package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.common.PageResult;
import com.hotel.dto.RoomTypeDTO;
import com.hotel.entity.RoomType;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.service.RoomTypeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomTypeServiceImpl extends ServiceImpl<RoomTypeMapper, RoomType> implements RoomTypeService {

    @Override
    public List<RoomType> listByHotelId(Long hotelId) {
        return lambdaQuery().eq(RoomType::getHotelId, hotelId)
                .eq(RoomType::getStatus, 1)
                .orderByAsc(RoomType::getPrice)
                .list();
    }

    @Override
    public RoomType getRoomTypeDetail(Long id) {
        RoomType roomType = getById(id);
        if (roomType == null) throw new BusinessException(404, "房型不存在");
        return roomType;
    }

    @Override
    public void createRoomType(RoomTypeDTO dto) {
        RoomType roomType = new RoomType();
        copyProperties(dto, roomType);
        roomType.setCreatedAt(LocalDateTime.now());
        roomType.setUpdatedAt(LocalDateTime.now());
        save(roomType);
    }

    @Override
    public void updateRoomType(Long id, RoomTypeDTO dto) {
        RoomType roomType = getById(id);
        if (roomType == null) throw new BusinessException(404, "房型不存在");
        copyProperties(dto, roomType);
        roomType.setUpdatedAt(LocalDateTime.now());
        updateById(roomType);
    }

    @Override
    public void deleteRoomType(Long id) {
        if (getById(id) == null) throw new BusinessException(404, "房型不存在");
        removeById(id);
    }

    @Override
    public PageResult<RoomType> adminListRoomTypes(Integer page, Integer size, Long hotelId, String keyword, Integer status) {
        Page<RoomType> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<>();
        if (hotelId != null) wrapper.eq(RoomType::getHotelId, hotelId);
        if (StringUtils.hasText(keyword)) wrapper.like(RoomType::getName, keyword);
        if (status != null) wrapper.eq(RoomType::getStatus, status);
        wrapper.orderByDesc(RoomType::getCreatedAt);
        Page<RoomType> result = page(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    private void copyProperties(RoomTypeDTO dto, RoomType entity) {
        entity.setHotelId(dto.getHotelId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setOriginalPrice(dto.getOriginalPrice());
        entity.setCapacity(dto.getCapacity() != null ? dto.getCapacity() : 2);
        entity.setBedType(dto.getBedType());
        entity.setArea(dto.getArea());
        entity.setFloor(dto.getFloor());
        entity.setFacilities(dto.getFacilities());
        entity.setImages(dto.getImages());
        entity.setBreakfast(dto.getBreakfast() != null ? dto.getBreakfast() : 0);
        entity.setHasWindow(dto.getHasWindow() != null ? dto.getHasWindow() : 1);
        entity.setTotalRooms(dto.getTotalRooms() != null ? dto.getTotalRooms() : 10);
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
    }
}
