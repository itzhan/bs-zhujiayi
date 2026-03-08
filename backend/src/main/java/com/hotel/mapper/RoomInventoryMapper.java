package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.RoomInventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RoomInventoryMapper extends BaseMapper<RoomInventory> {

    @Select("SELECT * FROM room_inventory WHERE room_type_id = #{roomTypeId} AND date BETWEEN #{startDate} AND #{endDate} ORDER BY date")
    List<RoomInventory> selectByRoomTypeAndDateRange(@Param("roomTypeId") Long roomTypeId,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);
}
