package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.Hotel;
import com.hotel.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{hotelId}")
    public Result<Void> toggleFavorite(Authentication authentication, @PathVariable Long hotelId) {
        Long userId = (Long) authentication.getPrincipal();
        favoriteService.toggleFavorite(userId, hotelId);
        return Result.success();
    }

    @GetMapping("/check/{hotelId}")
    public Result<Map<String, Boolean>> checkFavorite(Authentication authentication, @PathVariable Long hotelId) {
        Long userId = (Long) authentication.getPrincipal();
        boolean isFav = favoriteService.isFavorite(userId, hotelId);
        return Result.success(Map.of("isFavorite", isFav));
    }

    @GetMapping
    public Result<List<Hotel>> getMyFavorites(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(favoriteService.getUserFavorites(userId));
    }
}
