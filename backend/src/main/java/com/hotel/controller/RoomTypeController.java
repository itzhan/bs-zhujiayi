package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.RoomInventory;
import com.hotel.entity.RoomType;
import com.hotel.mapper.RoomInventoryMapper;
import com.hotel.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;
    private final RoomInventoryMapper roomInventoryMapper;

    @GetMapping("/{id}")
    public Result<RoomType> getRoomTypeDetail(@PathVariable Long id) {
        return Result.success(roomTypeService.getRoomTypeDetail(id));
    }

    @GetMapping("/{id}/inventory")
    public Result<List<RoomInventory>> getRoomInventory(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(roomInventoryMapper.selectByRoomTypeAndDateRange(id, startDate, endDate));
    }
}
