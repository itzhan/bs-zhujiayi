<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NButton, NDropdown, NAvatar, NBadge } from 'naive-ui'
import { PersonCircleOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const username = computed(() => userStore.username)

const navItems = [
  { label: '首页', path: '/' },
  { label: '搜索酒店', path: '/hotels' },
  { label: '公告', path: '/announcements' }
]

const userMenuOptions = [
  { label: '个人中心', key: '/user/profile' },
  { label: '我的订单', key: '/user/orders' },
  { label: '我的收藏', key: '/user/favorites' },
  { label: '我的评价', key: '/user/reviews' },
  { label: '我的优惠券', key: '/user/coupons' },
  { type: 'divider', key: 'd1' },
  { label: '退出登录', key: 'logout' }
]

function isActive(path: string) {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

function handleUserMenu(key: string) {
  if (key === 'logout') {
    userStore.logout()
    router.push('/')
  } else {
    router.push(key)
  }
}
</script>

<template>
  <header class="app-header">
    <div class="header-inner container">
      <div class="header-left">
        <router-link to="/" class="logo">
          <span class="logo-icon">H</span>
          <span class="logo-text">LuxStay</span>
        </router-link>
        <nav class="nav-links">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            :class="['nav-link', { active: isActive(item.path) }]"
          >
            {{ item.label }}
          </router-link>
        </nav>
      </div>

      <div class="header-right">
        <template v-if="isLoggedIn">
          <NDropdown
            :options="userMenuOptions"
            trigger="click"
            @select="handleUserMenu"
          >
            <div class="user-trigger">
              <NAvatar
                :size="32"
                round
                :style="{ backgroundColor: '#1B4965', cursor: 'pointer' }"
              >
                {{ username.charAt(0).toUpperCase() }}
              </NAvatar>
              <span class="user-name">{{ username }}</span>
            </div>
          </NDropdown>
        </template>
        <template v-else>
          <NButton text type="primary" @click="router.push('/login')">登录</NButton>
          <NButton
            type="primary"
            size="small"
            round
            @click="router.push('/register')"
            style="margin-left: 12px"
          >
            注册
          </NButton>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #FFFFFF;
  border-bottom: 1px solid var(--color-border);
  backdrop-filter: blur(8px);
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.logo-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-primary);
  color: #fff;
  font-weight: 800;
  font-size: 1.1rem;
  border-radius: 8px;
}

.logo-text {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-primary);
  letter-spacing: -0.5px;
}

.nav-links {
  display: flex;
  gap: 8px;
}

.nav-link {
  padding: 6px 16px;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--color-text-medium);
  text-decoration: none;
  transition: all 0.2s;
}

.nav-link:hover {
  color: var(--color-primary);
  background: rgba(27, 73, 101, 0.06);
}

.nav-link.active {
  color: var(--color-primary);
  background: rgba(27, 73, 101, 0.08);
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background 0.2s;
}

.user-trigger:hover {
  background: rgba(0, 0, 0, 0.04);
}

.user-name {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--color-text-dark);
}
</style>
