# 酒店预订管理系统 API 文档

## 概览
- Base URL: `http://localhost:8088`
- 认证方式: JWT Token (Bearer Token)
- Content-Type: `application/json`
- 分页参数: `page` (页码，从1开始), `size` (每页条数，默认10)

## 统一返回格式
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

## 分页返回格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

## 错误码
| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数校验失败 |
| 401 | 未认证/Token无效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 1. 认证接口 `/api/auth`

### 1.1 登录
- **POST** `/api/auth/login`
- **权限**: 公开
- **请求体**:
```json
{ "username": "admin", "password": "admin123" }
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1, "username": "admin", "nickname": "系统管理员",
      "email": "admin@hotel.com", "phone": "13800000001",
      "gender": 1, "role": 1, "status": 1
    }
  }
}
```

### 1.2 注册
- **POST** `/api/auth/register`
- **权限**: 公开
- **请求体**:
```json
{
  "username": "newuser",
  "password": "123456",
  "nickname": "新用户",
  "email": "newuser@qq.com",
  "phone": "13800001111"
}
```

---

## 2. 用户接口 `/api/user`

### 2.1 获取个人信息
- **GET** `/api/user/profile`
- **权限**: 需认证

### 2.2 更新个人信息
- **PUT** `/api/user/profile`
- **请求体**:
```json
{ "nickname": "新昵称", "email": "new@qq.com", "phone": "13800002222", "gender": 1 }
```

### 2.3 修改密码
- **PUT** `/api/user/password`
- **请求体**:
```json
{ "oldPassword": "123456", "newPassword": "654321" }
```

---

## 3. 酒店接口 `/api/hotels`

### 3.1 搜索酒店列表
- **GET** `/api/hotels`
- **权限**: 公开
- **参数**: `page`, `size`, `keyword`(关键词), `city`(城市), `starRating`(星级), `minPrice`, `maxPrice`, `sortBy`(排序: price_asc/price_desc/star_desc)

### 3.2 酒店详情
- **GET** `/api/hotels/{id}`
- **权限**: 公开

### 3.3 酒店房型列表
- **GET** `/api/hotels/{id}/room-types`
- **权限**: 公开

---

## 4. 房型接口 `/api/room-types`

### 4.1 房型详情
- **GET** `/api/room-types/{id}`
- **权限**: 公开

### 4.2 查询房型库存
- **GET** `/api/room-types/{id}/inventory?startDate=2026-02-08&endDate=2026-02-10`
- **权限**: 公开

---

## 5. 订单接口 `/api/orders`

### 5.1 创建订单
- **POST** `/api/orders`
- **权限**: 需认证
- **请求体**:
```json
{
  "hotelId": 1, "roomTypeId": 1,
  "checkInDate": "2026-03-01", "checkOutDate": "2026-03-03",
  "roomCount": 1, "guestName": "张三", "guestPhone": "13800000002",
  "couponId": null, "remark": "商务出差"
}
```

### 5.2 我的订单列表
- **GET** `/api/orders?page=1&size=10&status=`
- **权限**: 需认证
- **status**: 0待支付 1已支付待入住 2已入住 3已完成 4已取消 5退款中 6已退款

### 5.3 订单详情
- **GET** `/api/orders/{id}`
- **权限**: 需认证

### 5.4 取消订单
- **PUT** `/api/orders/{id}/cancel`
- **请求体**: `{ "reason": "行程变更" }`

### 5.5 申请退款
- **PUT** `/api/orders/{id}/refund`
- **请求体**: `{ "reason": "临时有事" }`

---

## 6. 支付接口 `/api/payments`

### 6.1 支付订单
- **POST** `/api/payments`
- **权限**: 需认证
- **请求体**:
```json
{ "orderId": 1, "paymentMethod": 1 }
```
- paymentMethod: 1微信 2支付宝 3银行卡

### 6.2 查询订单支付记录
- **GET** `/api/payments/order/{orderId}`

---

## 7. 评价接口 `/api/reviews`

### 7.1 发表评价
- **POST** `/api/reviews`
- **请求体**:
```json
{ "orderId": 1, "rating": 5, "content": "非常满意", "images": null }
```

### 7.2 酒店评价列表 (公开)
- **GET** `/api/reviews/hotel/{hotelId}?page=1&size=10`

### 7.3 我的评价列表
- **GET** `/api/reviews/my?page=1&size=10`

---

## 8. 优惠券接口 `/api/coupons`

### 8.1 可领取优惠券列表
- **GET** `/api/coupons/available`

### 8.2 领取优惠券
- **POST** `/api/coupons/{couponId}/receive`

### 8.3 我的优惠券
- **GET** `/api/coupons/my?status=` (status: 0未使用 1已使用 2已过期)

---

## 9. 收藏接口 `/api/favorites`

### 9.1 收藏/取消收藏
- **POST** `/api/favorites/{hotelId}` (切换状态)

### 9.2 检查收藏状态
- **GET** `/api/favorites/check/{hotelId}`

### 9.3 我的收藏列表
- **GET** `/api/favorites`

---

## 10. 公告接口 `/api/announcements`

### 10.1 已发布公告列表 (公开)
- **GET** `/api/announcements/published`

---

## 11. 文件上传 `/api/file`

### 11.1 上传文件
- **POST** `/api/file/upload`
- **Content-Type**: `multipart/form-data`
- **参数**: `file` (文件)
- **返回**: 文件访问URL

---

## 12. 管理员接口 `/api/admin` (需管理员权限)

### 12.1 用户管理
- **GET** `/api/admin/users?page=&size=&keyword=&role=&status=`
- **PUT** `/api/admin/users/{id}/status` - Body: `{ "status": 0/1 }`
- **PUT** `/api/admin/users/{id}/reset-password` - 重置密码为123456

### 12.2 酒店管理
- **GET** `/api/admin/hotels?page=&size=&keyword=&status=`
- **POST** `/api/admin/hotels`
- **PUT** `/api/admin/hotels/{id}`
- **DELETE** `/api/admin/hotels/{id}`
- **PUT** `/api/admin/hotels/{id}/status` - Body: `{ "status": 0/1 }`

### 12.3 房型管理
- **GET** `/api/admin/room-types?page=&size=&hotelId=&keyword=&status=`
- **POST** `/api/admin/room-types`
- **PUT** `/api/admin/room-types/{id}`
- **DELETE** `/api/admin/room-types/{id}`

### 12.4 订单管理
- **GET** `/api/admin/orders?page=&size=&orderNo=&status=&keyword=`
- **PUT** `/api/admin/orders/{id}/status` - Body: `{ "status": N }`
- **PUT** `/api/admin/orders/{id}/check-in` - 办理入住
- **PUT** `/api/admin/orders/{id}/check-out` - 办理退房
- **PUT** `/api/admin/orders/{id}/refund` - Body: `{ "approved": true/false, "reason": "..." }`

### 12.5 支付管理
- **GET** `/api/admin/payments?page=&size=&paymentNo=&status=`

### 12.6 评价管理
- **GET** `/api/admin/reviews?page=&size=&status=&hotelId=`
- **PUT** `/api/admin/reviews/{id}/audit` - Body: `{ "status": 1/2 }` (1通过 2拒绝)
- **PUT** `/api/admin/reviews/{id}/reply` - Body: `{ "reply": "感谢评价" }`

### 12.7 优惠券管理
- **GET** `/api/admin/coupons?page=&size=&status=`
- **POST** `/api/admin/coupons`
- **PUT** `/api/admin/coupons/{id}`
- **DELETE** `/api/admin/coupons/{id}`

### 12.8 公告管理
- **GET** `/api/admin/announcements?page=&size=&type=&status=`
- **POST** `/api/admin/announcements`
- **PUT** `/api/admin/announcements/{id}`
- **DELETE** `/api/admin/announcements/{id}`
- **PUT** `/api/admin/announcements/{id}/publish` - 发布

### 12.9 审计日志
- **GET** `/api/admin/audit-logs?page=&size=&action=&username=`

### 12.10 统计数据
- **GET** `/api/admin/statistics/dashboard` - 总览数据
- **GET** `/api/admin/statistics/revenue?startDate=2025-01-01` - 月度营收统计
- **GET** `/api/admin/statistics/hotel-ranking` - 酒店排名

---

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | zhangsan | 123456 |
| 普通用户 | lisi | 123456 |
| 普通用户 | wangwu | 123456 |
