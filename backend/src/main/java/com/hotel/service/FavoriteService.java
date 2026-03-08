package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.Favorite;
import com.hotel.entity.Hotel;

import java.util.List;

public interface FavoriteService extends IService<Favorite> {
    void toggleFavorite(Long userId, Long hotelId);
    boolean isFavorite(Long userId, Long hotelId);
    List<Hotel> getUserFavorites(Long userId);
}
