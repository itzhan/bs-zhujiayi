package com.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Favorite;
import com.hotel.entity.Hotel;
import com.hotel.mapper.FavoriteMapper;
import com.hotel.mapper.HotelMapper;
import com.hotel.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final HotelMapper hotelMapper;

    @Override
    public void toggleFavorite(Long userId, Long hotelId) {
        Favorite existing = lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getHotelId, hotelId)
                .one();
        if (existing != null) {
            removeById(existing.getId());
        } else {
            Favorite fav = new Favorite();
            fav.setUserId(userId);
            fav.setHotelId(hotelId);
            fav.setCreatedAt(LocalDateTime.now());
            save(fav);
        }
    }

    @Override
    public boolean isFavorite(Long userId, Long hotelId) {
        return lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getHotelId, hotelId)
                .count() > 0;
    }

    @Override
    public List<Hotel> getUserFavorites(Long userId) {
        List<Favorite> favorites = lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreatedAt)
                .list();
        if (favorites.isEmpty()) return Collections.emptyList();
        List<Long> hotelIds = favorites.stream().map(Favorite::getHotelId).collect(Collectors.toList());
        return hotelMapper.selectBatchIds(hotelIds);
    }
}
