package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.common.PageResult;
import com.hotel.dto.RoomTypeDTO;
import com.hotel.entity.RoomType;

import java.util.List;

public interface RoomTypeService extends IService<RoomType> {
    List<RoomType> listByHotelId(Long hotelId);
    RoomType getRoomTypeDetail(Long id);
    void createRoomType(RoomTypeDTO dto);
    void updateRoomType(Long id, RoomTypeDTO dto);
    void deleteRoomType(Long id);
    PageResult<RoomType> adminListRoomTypes(Integer page, Integer size, Long hotelId, String keyword, Integer status);
}
