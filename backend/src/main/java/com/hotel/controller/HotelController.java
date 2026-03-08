package com.hotel.controller;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.entity.Hotel;
import com.hotel.entity.RoomType;
import com.hotel.service.HotelService;
import com.hotel.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final RoomTypeService roomTypeService;

    @GetMapping
    public Result<PageResult<Hotel>> searchHotels(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer starRating,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String sortBy) {
        return Result.success(hotelService.searchHotels(page, size, keyword, city, starRating, minPrice, maxPrice, sortBy));
    }

    @GetMapping("/{id}")
    public Result<Hotel> getHotelDetail(@PathVariable Long id) {
        return Result.success(hotelService.getHotelDetail(id));
    }

    @GetMapping("/{id}/room-types")
    public Result<List<RoomType>> getHotelRoomTypes(@PathVariable Long id) {
        return Result.success(roomTypeService.listByHotelId(id));
    }
}
