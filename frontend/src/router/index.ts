import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/hotels',
    name: 'HotelList',
    component: () => import('@/views/HotelList.vue')
  },
  {
    path: '/hotels/:id',
    name: 'HotelDetail',
    component: () => import('@/views/HotelDetail.vue')
  },
  {
    path: '/booking',
    name: 'Booking',
    component: () => import('@/views/Booking.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/announcements',
    name: 'Announcements',
    component: () => import('@/views/Announcements.vue')
  },
  {
    path: '/user/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/orders',
    name: 'Orders',
    component: () => import('@/views/user/Orders.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/favorites',
    name: 'Favorites',
    component: () => import('@/views/user/Favorites.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/reviews',
    name: 'Reviews',
    component: () => import('@/views/user/Reviews.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/coupons',
    name: 'Coupons',
    component: () => import('@/views/user/Coupons.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
