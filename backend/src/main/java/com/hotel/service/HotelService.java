package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.common.PageResult;
import com.hotel.dto.HotelDTO;
import com.hotel.entity.Hotel;

public interface HotelService extends IService<Hotel> {
    PageResult<Hotel> searchHotels(Integer page, Integer size, String keyword, String city, Integer starRating, Integer minPrice, Integer maxPrice, String sortBy);
    Hotel getHotelDetail(Long id);
    void createHotel(HotelDTO dto);
    void updateHotel(Long id, HotelDTO dto);
    void deleteHotel(Long id);
    void updateHotelStatus(Long id, Integer status);
    PageResult<Hotel> adminListHotels(Integer page, Integer size, String keyword, Integer status);
}
