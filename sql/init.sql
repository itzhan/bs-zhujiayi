-- ============================================================
-- 酒店预订管理系统 - 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS hotel_booking DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE hotel_booking;

SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;

-- -----------------------------------------------------------
-- 1. 用户表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `audit_log`;
DROP TABLE IF EXISTS `user_coupon`;
DROP TABLE IF EXISTS `favorite`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `payment`;
DROP TABLE IF EXISTS `booking_order`;
DROP TABLE IF EXISTS `room_inventory`;
DROP TABLE IF EXISTS `room_type`;
DROP TABLE IF EXISTS `hotel`;
DROP TABLE IF EXISTS `coupon`;
DROP TABLE IF EXISTS `announcement`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0未知 1男 2女',
    `role` TINYINT DEFAULT 0 COMMENT '角色：0普通用户 1管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用 1正常',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- -----------------------------------------------------------
-- 2. 酒店表
-- -----------------------------------------------------------
CREATE TABLE `hotel` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '酒店ID',
    `name` VARCHAR(100) NOT NULL COMMENT '酒店名称',
    `description` TEXT DEFAULT NULL COMMENT '酒店描述',
    `address` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `city` VARCHAR(50) NOT NULL COMMENT '城市',
    `province` VARCHAR(50) DEFAULT NULL COMMENT '省份',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `star_rating` TINYINT DEFAULT 3 COMMENT '星级：1-5',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    `images` TEXT DEFAULT NULL COMMENT '图片列表(JSON数组)',
    `facilities` TEXT DEFAULT NULL COMMENT '设施列表(JSON数组)',
    `check_in_time` VARCHAR(10) DEFAULT '14:00' COMMENT '入住时间',
    `check_out_time` VARCHAR(10) DEFAULT '12:00' COMMENT '退房时间',
    `min_price` DECIMAL(10,2) DEFAULT NULL COMMENT '最低房价(冗余字段)',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0下架 1上架',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_city` (`city`),
    KEY `idx_star_rating` (`star_rating`),
    KEY `idx_status` (`status`),
    KEY `idx_min_price` (`min_price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='酒店表';

-- -----------------------------------------------------------
-- 3. 房型表
-- -----------------------------------------------------------
CREATE TABLE `room_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '房型ID',
    `hotel_id` BIGINT NOT NULL COMMENT '所属酒店ID',
    `name` VARCHAR(100) NOT NULL COMMENT '房型名称',
    `description` TEXT DEFAULT NULL COMMENT '房型描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '当前价格',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `capacity` INT DEFAULT 2 COMMENT '可住人数',
    `bed_type` VARCHAR(50) DEFAULT NULL COMMENT '床型',
    `area` DECIMAL(6,2) DEFAULT NULL COMMENT '面积(㎡)',
    `floor` VARCHAR(20) DEFAULT NULL COMMENT '楼层信息',
    `facilities` TEXT DEFAULT NULL COMMENT '房间设施(JSON数组)',
    `images` TEXT DEFAULT NULL COMMENT '图片列表(JSON数组)',
    `breakfast` TINYINT DEFAULT 0 COMMENT '是否含早餐：0否 1是',
    `has_window` TINYINT DEFAULT 1 COMMENT '是否有窗：0无 1有',
    `total_rooms` INT DEFAULT 10 COMMENT '总房间数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0下架 1上架',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_hotel_id` (`hotel_id`),
    KEY `idx_price` (`price`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_room_type_hotel` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='房型表';

-- -----------------------------------------------------------
-- 4. 房间库存日历表
-- -----------------------------------------------------------
CREATE TABLE `room_inventory` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '库存ID',
    `room_type_id` BIGINT NOT NULL COMMENT '房型ID',
    `date` DATE NOT NULL COMMENT '日期',
    `total_count` INT NOT NULL COMMENT '总间数',
    `booked_count` INT DEFAULT 0 COMMENT '已预订数',
    `price` DECIMAL(10,2) NOT NULL COMMENT '当日价格',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0不可订 1可订',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_date` (`room_type_id`, `date`),
    KEY `idx_date` (`date`),
    CONSTRAINT `fk_inventory_room_type` FOREIGN KEY (`room_type_id`) REFERENCES `room_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='房间库存日历表';

-- -----------------------------------------------------------
-- 5. 订单表
-- -----------------------------------------------------------
CREATE TABLE `booking_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `hotel_id` BIGINT NOT NULL COMMENT '酒店ID',
    `room_type_id` BIGINT NOT NULL COMMENT '房型ID',
    `hotel_name` VARCHAR(100) DEFAULT NULL COMMENT '酒店名称(冗余)',
    `room_type_name` VARCHAR(100) DEFAULT NULL COMMENT '房型名称(冗余)',
    `check_in_date` DATE NOT NULL COMMENT '入住日期',
    `check_out_date` DATE NOT NULL COMMENT '退房日期',
    `nights` INT NOT NULL COMMENT '入住天数',
    `room_count` INT DEFAULT 1 COMMENT '房间数',
    `guest_name` VARCHAR(50) NOT NULL COMMENT '入住人姓名',
    `guest_phone` VARCHAR(20) NOT NULL COMMENT '入住人电话',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总额',
    `coupon_id` BIGINT DEFAULT NULL COMMENT '优惠券ID',
    `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0待支付 1已支付待入住 2已入住 3已完成 4已取消 5退款中 6已退款',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `cancel_reason` VARCHAR(500) DEFAULT NULL COMMENT '取消原因',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_hotel_id` (`hotel_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_order_hotel` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`),
    CONSTRAINT `fk_order_room_type` FOREIGN KEY (`room_type_id`) REFERENCES `room_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单表';

-- -----------------------------------------------------------
-- 6. 支付记录表
-- -----------------------------------------------------------
CREATE TABLE `payment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付ID',
    `payment_no` VARCHAR(32) NOT NULL COMMENT '支付流水号',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号(冗余)',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `payment_method` TINYINT DEFAULT 1 COMMENT '支付方式：1微信 2支付宝 3银行卡',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0待支付 1已支付 2已退款 3支付失败',
    `paid_at` DATETIME DEFAULT NULL COMMENT '支付时间',
    `refund_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '退款金额',
    `refund_at` DATETIME DEFAULT NULL COMMENT '退款时间',
    `refund_reason` VARCHAR(500) DEFAULT NULL COMMENT '退款原因',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `booking_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='支付记录表';

-- -----------------------------------------------------------
-- 7. 评价表
-- -----------------------------------------------------------
CREATE TABLE `review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `hotel_id` BIGINT NOT NULL COMMENT '酒店ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `room_type_id` BIGINT NOT NULL COMMENT '房型ID',
    `rating` TINYINT NOT NULL COMMENT '评分：1-5',
    `content` TEXT DEFAULT NULL COMMENT '评价内容',
    `images` TEXT DEFAULT NULL COMMENT '评价图片(JSON数组)',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝',
    `reply` TEXT DEFAULT NULL COMMENT '管理员回复',
    `reply_at` DATETIME DEFAULT NULL COMMENT '回复时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_review` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_hotel_id` (`hotel_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_review_hotel` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`),
    CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `booking_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='评价表';

-- -----------------------------------------------------------
-- 8. 优惠券表
-- -----------------------------------------------------------
CREATE TABLE `coupon` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
    `title` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `type` TINYINT DEFAULT 1 COMMENT '类型：1满减 2折扣 3立减',
    `condition_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '使用门槛(满X元)',
    `discount_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '优惠金额(满减/立减)',
    `discount_rate` DECIMAL(3,2) DEFAULT NULL COMMENT '折扣率(如0.85)',
    `total_count` INT DEFAULT 0 COMMENT '发放总量(0无限)',
    `used_count` INT DEFAULT 0 COMMENT '已使用数',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='优惠券表';

-- -----------------------------------------------------------
-- 9. 用户优惠券关联表
-- -----------------------------------------------------------
CREATE TABLE `user_coupon` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `coupon_id` BIGINT NOT NULL COMMENT '优惠券ID',
    `order_id` BIGINT DEFAULT NULL COMMENT '使用的订单ID',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0未使用 1已使用 2已过期',
    `received_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    `used_at` DATETIME DEFAULT NULL COMMENT '使用时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_coupon_id` (`coupon_id`),
    CONSTRAINT `fk_uc_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_uc_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户优惠券关联表';

-- -----------------------------------------------------------
-- 10. 收藏表
-- -----------------------------------------------------------
CREATE TABLE `favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `hotel_id` BIGINT NOT NULL COMMENT '酒店ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_hotel` (`user_id`, `hotel_id`),
    CONSTRAINT `fk_fav_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_fav_hotel` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收藏表';

-- -----------------------------------------------------------
-- 11. 公告表
-- -----------------------------------------------------------
CREATE TABLE `announcement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `type` TINYINT DEFAULT 1 COMMENT '类型：1通知 2活动 3公告',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0草稿 1已发布 2已下架',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告表';

-- -----------------------------------------------------------
-- 12. 审计日志表
-- -----------------------------------------------------------
CREATE TABLE `audit_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '操作人用户名',
    `action` VARCHAR(100) NOT NULL COMMENT '操作动作',
    `target` VARCHAR(100) DEFAULT NULL COMMENT '操作对象',
    `detail` TEXT DEFAULT NULL COMMENT '操作详情',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_action` (`action`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审计日志表';
