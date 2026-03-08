<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NInput, NRate, NTag, NCarousel, NSpin } from 'naive-ui'
import { SearchOutline, LocationOutline, StarOutline, ArrowForwardOutline } from '@vicons/ionicons5'
import { searchHotels } from '@/api/hotel'
import { getAnnouncements } from '@/api/user'

const router = useRouter()
const keyword = ref('')
const featuredHotels = ref<any[]>([])
const announcements = ref<any[]>([])
const loading = ref(false)

async function loadFeaturedHotels() {
  loading.value = true
  try {
    const res: any = await searchHotels({ page: 1, size: 8, sortBy: 'rating' })
    featuredHotels.value = res.data?.records || res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function loadAnnouncements() {
  try {
    const res: any = await getAnnouncements()
    announcements.value = Array.isArray(res.data) ? res.data : res.data?.records || []
  } catch (e) {
    console.error(e)
  }
}

function handleSearch() {
  router.push({ path: '/hotels', query: { keyword: keyword.value } })
}

function goHotel(id: number) {
  router.push(`/hotels/${id}`)
}

onMounted(() => {
  loadFeaturedHotels()
  loadAnnouncements()
})
</script>

<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-bg"></div>
      <div class="hero-content container">
        <h1 class="hero-title">探索您的下一段旅程</h1>
        <p class="hero-subtitle">精选全球优质酒店，享受非凡住宿体验</p>
        <div class="search-box">
          <NInput
            v-model:value="keyword"
            placeholder="搜索城市、酒店名称..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <SearchOutline style="font-size: 20px; color: #94A3B8" />
            </template>
          </NInput>
          <NButton
            type="primary"
            size="large"
            style="background: #E07A5F; border-color: #E07A5F; min-width: 120px"
            @click="handleSearch"
          >
            搜索酒店
          </NButton>
        </div>
        <div class="hero-tags">
          <NTag
            v-for="city in ['北京', '上海', '杭州', '成都', '三亚', '厦门']"
            :key="city"
            round
            :bordered="false"
            size="medium"
            style="background: rgba(255,255,255,0.85); cursor: pointer; color: #1E293B"
            @click="router.push({ path: '/hotels', query: { city } })"
          >
            <template #icon>
              <LocationOutline style="font-size: 14px" />
            </template>
            {{ city }}
          </NTag>
        </div>
      </div>
    </section>

    <!-- Announcements Ticker -->
    <section class="announcements-bar" v-if="announcements.length">
      <div class="container announcements-inner">
        <NTag type="warning" size="small" round :bordered="false">
          公告
        </NTag>
        <div class="ticker-wrap">
          <div class="ticker">
            <span v-for="(a, i) in announcements" :key="i" class="ticker-item">
              {{ a.title || a.content }}
            </span>
          </div>
        </div>
        <router-link to="/announcements" class="more-link">查看全部</router-link>
      </div>
    </section>

    <!-- Featured Hotels -->
    <section class="section">
      <div class="container">
        <div class="section-header">
          <div>
            <h2 class="section-title">精选推荐</h2>
            <p class="section-subtitle">为您精心挑选的高品质住宿</p>
          </div>
          <NButton text type="primary" @click="router.push('/hotels')">
            查看全部
            <template #icon>
              <ArrowForwardOutline />
            </template>
          </NButton>
        </div>

        <NSpin :show="loading">
          <div class="hotel-grid">
            <div
              v-for="hotel in featuredHotels"
              :key="hotel.id"
              class="hotel-card"
              @click="goHotel(hotel.id)"
            >
              <div class="card-image">
                <img
                  :src="hotel.coverImage || hotel.image || 'https://picsum.photos/400/260?random=' + hotel.id"
                  :alt="hotel.name"
                />
                <div class="card-badge" v-if="hotel.starRating">
                  <StarOutline style="font-size: 12px" />
                  {{ hotel.starRating }}星
                </div>
              </div>
              <div class="card-body">
                <h3 class="card-title">{{ hotel.name }}</h3>
                <p class="card-location">
                  <LocationOutline style="font-size: 14px; vertical-align: -2px" />
                  {{ hotel.city || hotel.address || '暂无地址' }}
                </p>
                <div class="card-footer">
                  <div class="card-rating" v-if="hotel.rating">
                    <NRate
                      :value="hotel.rating"
                      :count="5"
                      allow-half
                      readonly
                      size="small"
                    />
                    <span class="rating-score">{{ hotel.rating?.toFixed(1) }}</span>
                  </div>
                  <div class="card-price">
                    <span class="price-label">起</span>
                    <span class="price">¥{{ hotel.minPrice || hotel.price || '--' }}</span>
                    <span class="price-unit">/晚</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </NSpin>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="container">
        <h2 class="section-title" style="text-align: center">为什么选择我们</h2>
        <div class="features-grid">
          <div class="feature-item">
            <div class="feature-icon" style="background: rgba(27, 73, 101, 0.1); color: #1B4965">
              🏨
            </div>
            <h3>精选酒店</h3>
            <p>严格筛选，只推荐品质出众的酒店</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon" style="background: rgba(224, 122, 95, 0.1); color: #E07A5F">
              💰
            </div>
            <h3>价格保障</h3>
            <p>同行最低价，差价双倍返还</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon" style="background: rgba(16, 185, 129, 0.1); color: #10B981">
              🎫
            </div>
            <h3>专属优惠</h3>
            <p>会员专享折扣与优惠券福利</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon" style="background: rgba(59, 130, 246, 0.1); color: #3B82F6">
              📞
            </div>
            <h3>贴心客服</h3>
            <p>7×24小时在线，随时为您服务</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* Hero */
.hero-section {
  position: relative;
  padding: 80px 0 60px;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #f0f4f8 0%, #e8edf5 50%, #f5f0eb 100%);
  z-index: 0;
}

.hero-bg::after {
  content: '';
  position: absolute;
  right: -10%;
  top: -20%;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(27, 73, 101, 0.06) 0%, transparent 70%);
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
}

.hero-title {
  font-size: 2.75rem;
  font-weight: 800;
  color: var(--color-text-dark);
  margin-bottom: 12px;
  letter-spacing: -1px;
}

.hero-subtitle {
  font-size: 1.15rem;
  color: var(--color-text-medium);
  margin-bottom: 36px;
}

.search-box {
  display: flex;
  gap: 12px;
  max-width: 640px;
  margin: 0 auto 24px;
  background: #fff;
  padding: 8px;
  border-radius: 14px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.hero-tags {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

/* Announcements */
.announcements-bar {
  background: #FFFBF5;
  border-bottom: 1px solid #F5E6D3;
  padding: 10px 0;
}

.announcements-inner {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ticker-wrap {
  flex: 1;
  overflow: hidden;
}

.ticker {
  display: flex;
  gap: 60px;
  animation: scroll-left 30s linear infinite;
  white-space: nowrap;
}

.ticker-item {
  font-size: 0.85rem;
  color: var(--color-text-medium);
}

.more-link {
  font-size: 0.8rem;
  white-space: nowrap;
}

@keyframes scroll-left {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}

/* Section Header */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}

/* Hotel Grid */
.hotel-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.card-image {
  position: relative;
  aspect-ratio: 16 / 11;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.hotel-card:hover .card-image img {
  transform: scale(1.05);
}

.card-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(4px);
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--color-star);
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-body {
  padding: 16px;
}

.card-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--color-text-dark);
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-location {
  font-size: 0.8rem;
  color: var(--color-text-medium);
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.card-rating {
  display: flex;
  align-items: center;
  gap: 6px;
}

.rating-score {
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--color-star);
}

.card-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-label {
  font-size: 0.7rem;
  color: var(--color-text-light);
}

.price-unit {
  font-size: 0.75rem;
  color: var(--color-text-medium);
}

/* Features */
.features-section {
  padding: 60px 0;
  background: #fff;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
  margin-top: 40px;
}

.feature-item {
  text-align: center;
  padding: 32px 20px;
  border-radius: var(--radius-md);
  transition: transform 0.2s;
}

.feature-item:hover {
  transform: translateY(-4px);
}

.feature-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.75rem;
  margin: 0 auto 16px;
}

.feature-item h3 {
  font-size: 1.05rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--color-text-dark);
}

.feature-item p {
  font-size: 0.85rem;
  color: var(--color-text-medium);
}

@media (max-width: 768px) {
  .hotel-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .hero-title {
    font-size: 2rem;
  }
  .footer-grid {
    grid-template-columns: 1fr;
  }
}
</style>
