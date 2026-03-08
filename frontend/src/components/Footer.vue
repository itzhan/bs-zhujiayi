<script setup lang="ts">
</script>

<template>
  <footer class="app-footer">
    <div class="footer-inner container">
      <div class="footer-grid">
        <div class="footer-col brand-col">
          <div class="footer-logo">
            <span class="logo-icon">H</span>
            <span class="logo-text">LuxStay</span>
          </div>
          <p class="footer-desc">
            精选全球优质酒店，为您提供舒适、便捷的住宿预订体验。
          </p>
        </div>
        <div class="footer-col">
          <h4>快速链接</h4>
          <router-link to="/">首页</router-link>
          <router-link to="/hotels">搜索酒店</router-link>
          <router-link to="/announcements">公告资讯</router-link>
        </div>
        <div class="footer-col">
          <h4>用户服务</h4>
          <router-link to="/user/orders">我的订单</router-link>
          <router-link to="/user/favorites">我的收藏</router-link>
          <router-link to="/user/coupons">优惠券</router-link>
        </div>
        <div class="footer-col">
          <h4>联系我们</h4>
          <p>客服热线：400-888-9999</p>
          <p>服务时间：7×24小时</p>
          <p>邮箱：service@luxstay.com</p>
        </div>
      </div>
      <div class="footer-bottom">
        <span>© 2026 LuxStay 酒店预订管理系统 · 毕业设计项目</span>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.app-footer {
  background: var(--color-text-dark);
  color: #94A3B8;
  padding: 48px 0 0;
  margin-top: 60px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1fr 1fr;
  gap: 40px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.footer-logo .logo-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-primary-hover);
  color: #fff;
  font-weight: 800;
  font-size: 1rem;
  border-radius: 6px;
}

.footer-logo .logo-text {
  font-size: 1.1rem;
  font-weight: 700;
  color: #E2E8F0;
}

.footer-desc {
  font-size: 0.875rem;
  line-height: 1.7;
}

.footer-col h4 {
  color: #E2E8F0;
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 16px;
}

.footer-col a,
.footer-col p {
  display: block;
  font-size: 0.85rem;
  color: #94A3B8;
  margin-bottom: 10px;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-col a:hover {
  color: #E2E8F0;
}

.footer-bottom {
  margin-top: 40px;
  padding: 20px 0;
  border-top: 1px solid rgba(148, 163, 184, 0.15);
  text-align: center;
  font-size: 0.8rem;
}
</style>
