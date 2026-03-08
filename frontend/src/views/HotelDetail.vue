<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NButton, NRate, NTag, NTabs, NTabPane, NImage, NImageGroup,
  NPagination, NSpin, NEmpty, NAvatar, useMessage, NDivider
} from 'naive-ui'
import {
  HeartOutline, Heart, LocationOutline, CallOutline,
  WifiOutline, CarOutline, RestaurantOutline, FitnessOutline,
  StarOutline
} from '@vicons/ionicons5'
import { getHotelDetail, getHotelRoomTypes } from '@/api/hotel'
import { getHotelReviews } from '@/api/review'
import { toggleFavorite, checkFavorite } from '@/api/favorite'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const hotelId = Number(route.params.id)
const hotel = ref<any>({})
const roomTypes = ref<any[]>([])
const reviews = ref<any[]>([])
const reviewTotal = ref(0)
const reviewPage = ref(1)
const isFavorited = ref(false)
const loading = ref(true)

const images = computed(() => {
  if (hotel.value.images && Array.isArray(hotel.value.images)) return hotel.value.images
  if (hotel.value.coverImage) return [hotel.value.coverImage]
  return [`https://picsum.photos/800/400?random=${hotelId}`]
})

async function loadHotel() {
  loading.value = true
  try {
    const res: any = await getHotelDetail(hotelId)
    hotel.value = res.data
  } catch (e) {
    message.error('加载酒店信息失败')
  } finally {
    loading.value = false
  }
}

async function loadRoomTypes() {
  try {
    const res: any = await getHotelRoomTypes(hotelId)
    roomTypes.value = Array.isArray(res.data) ? res.data : res.data?.records || []
  } catch (e) {
    console.error(e)
  }
}

async function loadReviews() {
  try {
    const res: any = await getHotelReviews(hotelId, { page: reviewPage.value, size: 10 })
    reviews.value = res.data?.records || res.data || []
    reviewTotal.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  }
}

async function loadFavoriteStatus() {
  if (!userStore.isLoggedIn) return
  try {
    const res: any = await checkFavorite(hotelId)
    isFavorited.value = !!res.data
  } catch (e) {
    // ignore
  }
}

async function handleToggleFavorite() {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await toggleFavorite(hotelId)
    isFavorited.value = !isFavorited.value
    message.success(isFavorited.value ? '已收藏' : '已取消收藏')
  } catch (e) {
    message.error('操作失败')
  }
}

function scrollToRooms() {
  document.getElementById('rooms')?.scrollIntoView({ behavior: 'smooth' })
}

function goBook(roomType: any) {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  router.push({
    path: '/booking',
    query: {
      hotelId: String(hotelId),
      roomTypeId: String(roomType.id),
      hotelName: hotel.value.name,
      roomName: roomType.name,
      price: String(roomType.price)
    }
  })
}

onMounted(() => {
  loadHotel()
  loadRoomTypes()
  loadReviews()
  loadFavoriteStatus()
})
</script>

<template>
  <div class="hotel-detail-page">
    <NSpin :show="loading">
      <div class="container" style="padding-top: 32px">
        <!-- Gallery -->
        <div class="gallery-section">
          <NImageGroup>
            <div class="gallery-grid">
              <div class="gallery-main">
                <NImage
                  :src="images[0]"
                  object-fit="cover"
                  width="100%"
                  height="400"
                  :img-props="{ style: 'border-radius: 12px 0 0 12px' }"
                />
              </div>
              <div class="gallery-side" v-if="images.length > 1">
                <NImage
                  v-for="(img, i) in images.slice(1, 5)"
                  :key="i"
                  :src="img"
                  object-fit="cover"
                  width="100%"
                  height="196"
                />
              </div>
            </div>
          </NImageGroup>
        </div>

        <!-- Hotel Info -->
        <div class="info-section">
          <div class="info-main">
            <div class="info-header">
              <div>
                <div class="info-tags">
                  <NTag v-if="hotel.starRating" type="warning" size="small" :bordered="false">
                    {{ hotel.starRating }}星级酒店
                  </NTag>
                  <NTag v-if="hotel.city" size="small" :bordered="false">
                    {{ hotel.city }}
                  </NTag>
                </div>
                <h1 class="hotel-name">{{ hotel.name }}</h1>
                <p class="hotel-addr">
                  <LocationOutline style="font-size: 16px; vertical-align: -3px" />
                  {{ hotel.address || '暂无地址' }}
                </p>
              </div>
              <div class="info-actions">
                <NButton
                  :type="isFavorited ? 'error' : 'default'"
                  :ghost="!isFavorited"
                  round
                  @click="handleToggleFavorite"
                >
                  <template #icon>
                    <Heart v-if="isFavorited" />
                    <HeartOutline v-else />
                  </template>
                  {{ isFavorited ? '已收藏' : '收藏' }}
                </NButton>
              </div>
            </div>

            <div class="hotel-rating-bar" v-if="hotel.rating">
              <div class="rating-big">
                <span class="rating-number">{{ hotel.rating?.toFixed(1) }}</span>
                <span class="rating-label">综合评分</span>
              </div>
              <NRate :value="hotel.rating" allow-half readonly size="large" />
              <span class="review-count">{{ reviewTotal }} 条评价</span>
            </div>

            <p class="hotel-description" v-if="hotel.description">
              {{ hotel.description }}
            </p>

            <!-- Facilities -->
            <div class="facilities" v-if="hotel.facilities">
              <h3>酒店设施</h3>
              <div class="facility-tags">
                <NTag
                  v-for="f in (typeof hotel.facilities === 'string' ? hotel.facilities.split(',') : hotel.facilities)"
                  :key="f"
                  size="medium"
                  :bordered="false"
                  round
                  style="background: rgba(27,73,101,0.06); color: #1B4965"
                >
                  {{ f }}
                </NTag>
              </div>
            </div>
          </div>

          <div class="info-sidebar">
            <div class="price-card">
              <div class="price-card-label">参考价格</div>
              <div class="price-card-value price-large">
                ¥{{ hotel.minPrice || hotel.price || '--' }}
              </div>
              <div class="price-card-sub">起/每晚</div>
              <NButton
                type="primary"
                block
                size="large"
                style="background: #E07A5F; border-color: #E07A5F; margin-top: 16px"
                @click="scrollToRooms"
              >
                查看房型
              </NButton>
              <p v-if="hotel.phone" class="hotel-phone">
                <CallOutline style="font-size: 14px; vertical-align: -2px" />
                {{ hotel.phone }}
              </p>
            </div>
          </div>
        </div>

        <NDivider />

        <!-- Tabs: Room Types & Reviews -->
        <NTabs type="line" size="large" animated>
          <NTabPane name="rooms" tab="房型列表" id="rooms">
            <div class="room-list">
              <div v-for="room in roomTypes" :key="room.id" class="room-card">
                <div class="room-image">
                  <img
                    :src="room.image || room.coverImage || 'https://picsum.photos/300/200?random=' + room.id"
                    :alt="room.name"
                  />
                </div>
                <div class="room-info">
                  <h3 class="room-name">{{ room.name }}</h3>
                  <div class="room-tags">
                    <NTag v-if="room.bedType" size="small" :bordered="false">{{ room.bedType }}</NTag>
                    <NTag v-if="room.area" size="small" :bordered="false">{{ room.area }}m²</NTag>
                    <NTag v-if="room.maxGuests" size="small" :bordered="false">入住{{ room.maxGuests }}人</NTag>
                    <NTag v-if="room.breakfast" type="success" size="small" :bordered="false">含早餐</NTag>
                  </div>
                  <p class="room-desc" v-if="room.description">{{ room.description }}</p>
                </div>
                <div class="room-action">
                  <div class="room-price">
                    <span class="price">¥{{ room.price }}</span>
                    <span class="price-unit">/晚</span>
                  </div>
                  <NButton
                    type="primary"
                    style="background: #E07A5F; border-color: #E07A5F"
                    @click="goBook(room)"
                  >
                    立即预订
                  </NButton>
                </div>
              </div>
              <NEmpty v-if="!roomTypes.length" description="暂无房型信息" />
            </div>
          </NTabPane>

          <NTabPane name="reviews" tab="住客评价">
            <div class="review-list">
              <div v-for="review in reviews" :key="review.id" class="review-item">
                <div class="review-header">
                  <NAvatar
                    :size="40"
                    round
                    :style="{ backgroundColor: '#1B4965' }"
                  >
                    {{ (review.nickname || review.username || '匿').charAt(0) }}
                  </NAvatar>
                  <div class="review-meta">
                    <span class="review-author">{{ review.nickname || review.username || '匿名用户' }}</span>
                    <span class="review-date">{{ review.createTime || review.createdAt || '' }}</span>
                  </div>
                  <NRate :value="review.rating" allow-half readonly size="small" />
                </div>
                <p class="review-content">{{ review.content }}</p>
              </div>
              <NEmpty v-if="!reviews.length" description="暂无评价" />
            </div>
            <div class="pagination-wrap" v-if="reviewTotal > 10">
              <NPagination
                v-model:page="reviewPage"
                :page-count="Math.ceil(reviewTotal / 10)"
                @update:page="loadReviews"
              />
            </div>
          </NTabPane>
        </NTabs>
      </div>
    </NSpin>
  </div>
</template>

<style scoped>
.gallery-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 4px;
  border-radius: 12px;
  overflow: hidden;
}

.gallery-side {
  display: grid;
  grid-template-rows: 1fr 1fr;
  gap: 4px;
}

/* Info Section */
.info-section {
  display: flex;
  gap: 32px;
  margin-top: 28px;
}

.info-main {
  flex: 1;
}

.info-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.info-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
}

.hotel-name {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--color-text-dark);
  margin-bottom: 8px;
}

.hotel-addr {
  font-size: 0.9rem;
  color: var(--color-text-medium);
  display: flex;
  align-items: center;
  gap: 4px;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.hotel-rating-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 20px 0;
  padding: 16px 20px;
  background: rgba(245, 158, 11, 0.06);
  border-radius: var(--radius-md);
}

.rating-big {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.rating-number {
  font-size: 2rem;
  font-weight: 800;
  color: var(--color-star);
  line-height: 1;
}

.rating-label {
  font-size: 0.75rem;
  color: var(--color-text-medium);
  margin-top: 4px;
}

.review-count {
  font-size: 0.85rem;
  color: var(--color-text-medium);
}

.hotel-description {
  font-size: 0.95rem;
  color: var(--color-text-medium);
  line-height: 1.8;
  margin: 16px 0;
}

.facilities h3 {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 12px;
}

.facility-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* Price Card */
.price-card {
  background: #fff;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 24px;
  text-align: center;
  position: sticky;
  top: 96px;
}

.price-card-label {
  font-size: 0.85rem;
  color: var(--color-text-medium);
}

.price-card-value {
  margin: 4px 0;
}

.price-card-sub {
  font-size: 0.8rem;
  color: var(--color-text-light);
}

.hotel-phone {
  margin-top: 16px;
  font-size: 0.85rem;
  color: var(--color-text-medium);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

/* Room List */
.room-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.room-card {
  display: flex;
  background: #fff;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
  transition: box-shadow 0.2s;
}

.room-card:hover {
  box-shadow: var(--shadow-md);
}

.room-image {
  width: 220px;
  flex-shrink: 0;
}

.room-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.room-info {
  flex: 1;
  padding: 16px 20px;
}

.room-name {
  font-size: 1.05rem;
  font-weight: 600;
  margin-bottom: 8px;
}

.room-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.room-desc {
  font-size: 0.85rem;
  color: var(--color-text-medium);
}

.room-action {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  padding: 16px 24px;
  min-width: 140px;
}

.room-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
  margin-bottom: 12px;
}

.room-price .price {
  font-size: 1.5rem;
}

.price-unit {
  font-size: 0.8rem;
  color: var(--color-text-medium);
}

/* Reviews */
.review-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 16px;
}

.review-item {
  padding: 16px;
  background: #fff;
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.review-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.review-author {
  font-weight: 600;
  font-size: 0.9rem;
}

.review-date {
  font-size: 0.8rem;
  color: var(--color-text-light);
}

.review-content {
  font-size: 0.9rem;
  color: var(--color-text-medium);
  line-height: 1.7;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

@media (max-width: 768px) {
  .info-section {
    flex-direction: column;
  }
  .info-sidebar {
    width: 100%;
  }
  .gallery-grid {
    grid-template-columns: 1fr;
  }
  .room-card {
    flex-direction: column;
  }
  .room-image {
    width: 100%;
    height: 180px;
  }
}
</style>
