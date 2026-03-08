package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.common.PageResult;
import com.hotel.dto.HotelDTO;
import com.hotel.entity.Hotel;
import com.hotel.mapper.HotelMapper;
import com.hotel.service.HotelService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements HotelService {

    @Override
    public PageResult<Hotel> searchHotels(Integer page, Integer size, String keyword, String city,
                                           Integer starRating, Integer minPrice, Integer maxPrice, String sortBy) {
        Page<Hotel> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getStatus, 1); // 只展示上架的
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Hotel::getName, keyword).or().like(Hotel::getAddress, keyword));
        }
        if (StringUtils.hasText(city)) wrapper.eq(Hotel::getCity, city);
        if (starRating != null) wrapper.eq(Hotel::getStarRating, starRating);
        if (minPrice != null) wrapper.ge(Hotel::getMinPrice, minPrice);
        if (maxPrice != null) wrapper.le(Hotel::getMinPrice, maxPrice);

        if ("price_asc".equals(sortBy)) {
            wrapper.orderByAsc(Hotel::getMinPrice);
        } else if ("price_desc".equals(sortBy)) {
            wrapper.orderByDesc(Hotel::getMinPrice);
        } else if ("star_desc".equals(sortBy)) {
            wrapper.orderByDesc(Hotel::getStarRating);
        } else {
            wrapper.orderByDesc(Hotel::getCreatedAt);
        }

        Page<Hotel> result = page(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Hotel getHotelDetail(Long id) {
        Hotel hotel = getById(id);
        if (hotel == null) throw new BusinessException(404, "酒店不存在");
        return hotel;
    }

    @Override
    public void createHotel(HotelDTO dto) {
        Hotel hotel = new Hotel();
        copyProperties(dto, hotel);
        hotel.setCreatedAt(LocalDateTime.now());
        hotel.setUpdatedAt(LocalDateTime.now());
        save(hotel);
    }

    @Override
    public void updateHotel(Long id, HotelDTO dto) {
        Hotel hotel = getById(id);
        if (hotel == null) throw new BusinessException(404, "酒店不存在");
        copyProperties(dto, hotel);
        hotel.setUpdatedAt(LocalDateTime.now());
        updateById(hotel);
    }

    @Override
    public void deleteHotel(Long id) {
        if (getById(id) == null) throw new BusinessException(404, "酒店不存在");
        removeById(id);
    }

    @Override
    public void updateHotelStatus(Long id, Integer status) {
        Hotel hotel = getById(id);
        if (hotel == null) throw new BusinessException(404, "酒店不存在");
        hotel.setStatus(status);
        hotel.setUpdatedAt(LocalDateTime.now());
        updateById(hotel);
    }

    @Override
    public PageResult<Hotel> adminListHotels(Integer page, Integer size, String keyword, Integer status) {
        Page<Hotel> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Hotel::getName, keyword).or().like(Hotel::getCity, keyword));
        }
        if (status != null) wrapper.eq(Hotel::getStatus, status);
        wrapper.orderByDesc(Hotel::getCreatedAt);
        Page<Hotel> result = page(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    private void copyProperties(HotelDTO dto, Hotel hotel) {
        hotel.setName(dto.getName());
        hotel.setDescription(dto.getDescription());
        hotel.setAddress(dto.getAddress());
        hotel.setCity(dto.getCity());
        hotel.setProvince(dto.getProvince());
        hotel.setPhone(dto.getPhone());
        hotel.setStarRating(dto.getStarRating());
        hotel.setCoverImage(dto.getCoverImage());
        hotel.setImages(dto.getImages());
        hotel.setFacilities(dto.getFacilities());
        hotel.setCheckInTime(dto.getCheckInTime() != null ? dto.getCheckInTime() : "14:00");
        hotel.setCheckOutTime(dto.getCheckOutTime() != null ? dto.getCheckOutTime() : "12:00");
        hotel.setMinPrice(dto.getMinPrice());
        hotel.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
    }
}
