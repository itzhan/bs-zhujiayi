package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.common.PageResult;
import com.hotel.dto.OrderDTO;
import com.hotel.entity.*;
import com.hotel.mapper.BookingOrderMapper;
import com.hotel.mapper.RoomInventoryMapper;
import com.hotel.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<BookingOrderMapper, BookingOrder> implements OrderService {

    private final HotelService hotelService;
    private final RoomTypeService roomTypeService;
    private final RoomInventoryMapper roomInventoryMapper;

    @Override
    @Transactional
    public BookingOrder createOrder(Long userId, OrderDTO dto) {
        // 校验日期
        if (!dto.getCheckOutDate().isAfter(dto.getCheckInDate())) {
            throw new BusinessException("退房日期必须晚于入住日期");
        }
        if (dto.getCheckInDate().isBefore(LocalDate.now())) {
            throw new BusinessException("入住日期不能早于今天");
        }

        Hotel hotel = hotelService.getHotelDetail(dto.getHotelId());
        RoomType roomType = roomTypeService.getRoomTypeDetail(dto.getRoomTypeId());
        if (!roomType.getHotelId().equals(hotel.getId())) {
            throw new BusinessException("房型不属于该酒店");
        }

        long nights = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        int roomCount = dto.getRoomCount() != null ? dto.getRoomCount() : 1;

        // 检查库存并计算价格
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<RoomInventory> inventories = roomInventoryMapper.selectByRoomTypeAndDateRange(
                roomType.getId(), dto.getCheckInDate(), dto.getCheckOutDate().minusDays(1));

        if (inventories.size() < nights) {
            // 库存记录不足，使用房型默认价格
            totalAmount = roomType.getPrice().multiply(BigDecimal.valueOf(nights)).multiply(BigDecimal.valueOf(roomCount));
        } else {
            for (RoomInventory inv : inventories) {
                if (inv.getTotalCount() - inv.getBookedCount() < roomCount) {
                    throw new BusinessException("日期 " + inv.getDate() + " 库存不足");
                }
                totalAmount = totalAmount.add(inv.getPrice().multiply(BigDecimal.valueOf(roomCount)));
            }
            // 扣减库存
            for (RoomInventory inv : inventories) {
                inv.setBookedCount(inv.getBookedCount() + roomCount);
                inv.setUpdatedAt(LocalDateTime.now());
                roomInventoryMapper.updateById(inv);
            }
        }

        // 计算优惠
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal actualAmount = totalAmount.subtract(discountAmount);

        // 生成订单
        BookingOrder order = new BookingOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setHotelId(hotel.getId());
        order.setRoomTypeId(roomType.getId());
        order.setHotelName(hotel.getName());
        order.setRoomTypeName(roomType.getName());
        order.setCheckInDate(dto.getCheckInDate());
        order.setCheckOutDate(dto.getCheckOutDate());
        order.setNights((int) nights);
        order.setRoomCount(roomCount);
        order.setGuestName(dto.getGuestName());
        order.setGuestPhone(dto.getGuestPhone());
        order.setTotalAmount(totalAmount);
        order.setCouponId(dto.getCouponId());
        order.setDiscountAmount(discountAmount);
        order.setActualAmount(actualAmount);
        order.setStatus(0); // 待支付
        order.setRemark(dto.getRemark());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        save(order);
        return order;
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId, String reason) {
        BookingOrder order = getById(orderId);
        if (order == null) throw new BusinessException(404, "订单不存在");
        if (!order.getUserId().equals(userId)) throw new BusinessException(403, "无权操作此订单");
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new BusinessException("当前订单状态不可取消");
        }
        order.setStatus(4); // 已取消
        order.setCancelReason(reason);
        order.setUpdatedAt(LocalDateTime.now());
        updateById(order);

        // 恢复库存
        restoreInventory(order);
    }

    @Override
    @Transactional
    public void requestRefund(Long userId, Long orderId, String reason) {
        BookingOrder order = getById(orderId);
        if (order == null) throw new BusinessException(404, "订单不存在");
        if (!order.getUserId().equals(userId)) throw new BusinessException(403, "无权操作此订单");
        if (order.getStatus() != 1) throw new BusinessException("只有已支付待入住的订单才能申请退款");
        order.setStatus(5); // 退款中
        order.setCancelReason(reason);
        order.setUpdatedAt(LocalDateTime.now());
        updateById(order);
    }

    @Override
    public PageResult<BookingOrder> getUserOrders(Long userId, Integer page, Integer size, Integer status) {
        Page<BookingOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getUserId, userId);
        if (status != null) wrapper.eq(BookingOrder::getStatus, status);
        wrapper.orderByDesc(BookingOrder::getCreatedAt);
        Page<BookingOrder> result = page(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public BookingOrder getOrderDetail(Long userId, Long orderId) {
        BookingOrder order = getById(orderId);
        if (order == null) throw new BusinessException(404, "订单不存在");
        if (!order.getUserId().equals(userId)) throw new BusinessException(403, "无权查看此订单");
        return order;
    }

    @Override
    public PageResult<BookingOrder> adminListOrders(Integer page, Integer size, String orderNo,
                                                     Integer status, String keyword) {
        Page<BookingOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderNo)) wrapper.like(BookingOrder::getOrderNo, orderNo);
        if (status != null) wrapper.eq(BookingOrder::getStatus, status);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(BookingOrder::getGuestName, keyword)
                    .or().like(BookingOrder::getHotelName, keyword));
        }
        wrapper.orderByDesc(BookingOrder::getCreatedAt);
        Page<BookingOrder> result = page(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional
    public void adminUpdateOrderStatus(Long orderId, Integer status) {
        BookingOrder order = getById(orderId);
        if (order == null) throw new BusinessException(404, "订单不存在");
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void adminProcessRefund(Long orderId, boolean approved, String reason) {
        BookingOrder order = getById(orderId);
        if (order == null) throw new BusinessException(404, "订单不存在");
        if (order.getStatus() != 5) throw new BusinessException("订单不在退款中状态");
        if (approved) {
            order.setStatus(6); // 已退款
            restoreInventory(order);
        } else {
            order.setStatus(1); // 恢复为已支付
            order.setCancelReason(reason);
        }
        order.setUpdatedAt(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void checkIn(Long orderId) {
        BookingOrder order = getById(orderId);
        if (order == null) throw new BusinessException(404, "订单不存在");
        if (order.getStatus() != 1) throw new BusinessException("订单状态不允许办理入住");
        order.setStatus(2);
        order.setUpdatedAt(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void checkOut(Long orderId) {
        BookingOrder order = getById(orderId);
        if (order == null) throw new BusinessException(404, "订单不存在");
        if (order.getStatus() != 2) throw new BusinessException("订单状态不允许办理退房");
        order.setStatus(3);
        order.setUpdatedAt(LocalDateTime.now());
        updateById(order);
    }

    private void restoreInventory(BookingOrder order) {
        List<RoomInventory> inventories = roomInventoryMapper.selectByRoomTypeAndDateRange(
                order.getRoomTypeId(), order.getCheckInDate(), order.getCheckOutDate().minusDays(1));
        for (RoomInventory inv : inventories) {
            inv.setBookedCount(Math.max(0, inv.getBookedCount() - order.getRoomCount()));
            inv.setUpdatedAt(LocalDateTime.now());
            roomInventoryMapper.updateById(inv);
        }
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "ORD" + date + random;
    }
}
