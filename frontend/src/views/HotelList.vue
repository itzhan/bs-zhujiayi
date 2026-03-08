<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NInput, NButton, NSelect, NSlider, NRate, NTag, NPagination,
  NSpin, NEmpty, NInputNumber
} from 'naive-ui'
import { SearchOutline, LocationOutline, StarOutline, FunnelOutline } from '@vicons/ionicons5'
import { searchHotels } from '@/api/hotel'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const hotels = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(12)

const keyword = ref((route.query.keyword as string) || '')
const city = ref((route.query.city as string) || '')
const starRating = ref<number | null>(null)
const minPrice = ref<number | undefined>(undefined)
const maxPrice = ref<number | undefined>(undefined)
const sortBy = ref('')
const showFilter = ref(false)

const cityOptions = [
  { label: '全部城市', value: '' },
  { label: '北京', value: '北京' },
  { label: '上海', value: '上海' },
  { label: '广州', value: '广州' },
  { label: '深圳', value: '深圳' },
  { label: '杭州', value: '杭州' },
  { label: '成都', value: '成都' },
  { label: '三亚', value: '三亚' },
  { label: '厦门', value: '厦门' },
  { label: '重庆', value: '重庆' },
  { label: '西安', value: '西安' }
]

const starOptions = [
  { label: '不限', value: null },
  { label: '五星级', value: 5 },
  { label: '四星级', value: 4 },
  { label: '三星级', value: 3 },
  { label: '二星级', value: 2 }
]

const sortOptions = [
  { label: '默认排序', value: '' },
  { label: '价格从低到高', value: 'price_asc' },
  { label: '价格从高到低', value: 'price_desc' },
  { label: '评分最高', value: 'rating' }
]

async function loadHotels() {
  loading.value = true
  try {
    const params: any = {
      page: page.value,
      size: size.value
    }
    if (keyword.value) params.keyword = keyword.value
    if (city.value) params.city = city.value
    if (starRating.value) params.starRating = starRating.value
    if (minPrice.value !== undefined) params.minPrice = minPrice.value
    if (maxPrice.value !== undefined) params.maxPrice = maxPrice.value
    if (sortBy.value) params.sortBy = sortBy.value

    const res: any = await searchHotels(params)
    hotels.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  loadHotels()
}

function resetFilters() {
  keyword.value = ''
  city.value = ''
  starRating.value = null
  minPrice.value = undefined
  maxPrice.value = undefined
  sortBy.value = ''
  page.value = 1
  loadHotels()
}

function goHotel(id: number) {
  router.push(`/hotels/${id}`)
}

watch(page, loadHotels)

onMounted(loadHotels)
</script>

<template>
  <div class="hotel-list-page">
    <!-- Search Bar -->
    <section class="search-section">
      <div class="container">
        <div class="search-bar">
          <NInput
            v-model:value="keyword"
            placeholder="搜索酒店名称、地址..."
            clearable
            size="large"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <SearchOutline style="font-size: 18px; color: #94A3B8" />
            </template>
          </NInput>
          <NSelect
            v-model:value="city"
            :options="cityOptions"
            placeholder="选择城市"
            size="large"
            style="width: 160px"
            @update:value="handleSearch"
          />
          <NSelect
            v-model:value="sortBy"
            :options="sortOptions"
            placeholder="排序"
            size="large"
            style="width: 160px"
            @update:value="handleSearch"
          />
          <NButton
            type="primary"
            size="large"
            @click="handleSearch"
            style="min-width: 100px"
          >
            搜索
          </NButton>
          <NButton
            size="large"
            quaternary
            @click="showFilter = !showFilter"
          >
            <template #icon>
              <FunnelOutline />
            </template>
            筛选
          </NButton>
        </div>

        <!-- Advanced Filters -->
        <transition name="slide">
          <div class="filter-panel" v-if="showFilter">
            <div class="filter-row">
              <div class="filter-item">
                <label>星级</label>
                <div class="star-btns">
                  <NButton
                    v-for="opt in starOptions"
                    :key="String(opt.value)"
                    :type="starRating === opt.value ? 'primary' : 'default'"
                    size="small"
                    @click="starRating = opt.value; handleSearch()"
                  >
                    {{ opt.label }}
                  </NButton>
                </div>
              </div>
              <div class="filter-item">
                <label>价格范围</label>
                <div class="price-range">
                  <NInputNumber
                    v-model:value="minPrice"
                    placeholder="最低"
                    :min="0"
                    size="small"
                    style="width: 120px"
                  />
                  <span style="color: #CBD5E1">—</span>
                  <NInputNumber
                    v-model:value="maxPrice"
                    placeholder="最高"
                    :min="0"
                    size="small"
                    style="width: 120px"
                  />
                  <NButton size="small" type="primary" @click="handleSearch">确定</NButton>
                </div>
              </div>
            </div>
            <NButton text size="small" @click="resetFilters" style="margin-top: 8px">
              重置筛选
            </NButton>
          </div>
        </transition>
      </div>
    </section>

    <!-- Results -->
    <section class="results-section">
      <div class="container">
        <div class="results-header">
          <span class="results-count" v-if="!loading">
            共找到 <strong>{{ total }}</strong> 家酒店
          </span>
        </div>

        <NSpin :show="loading">
          <div v-if="hotels.length" class="hotel-grid">
            <div
              v-for="hotel in hotels"
              :key="hotel.id"
              class="hotel-card-h"
              @click="goHotel(hotel.id)"
            >
              <div class="card-h-image">
                <img
                  :src="hotel.coverImage || hotel.image || 'https://picsum.photos/400/300?random=' + hotel.id"
                  :alt="hotel.name"
                />
              </div>
              <div class="card-h-body">
                <div class="card-h-top">
                  <h3 class="card-h-title">{{ hotel.name }}</h3>
                  <div class="card-h-tags">
                    <NTag v-if="hotel.starRating" size="small" :bordered="false" type="warning">
                      {{ hotel.starRating }}星级
                    </NTag>
                    <NTag v-if="hotel.city" size="small" :bordered="false">
                      {{ hotel.city }}
                    </NTag>
                  </div>
                  <p class="card-h-addr">
                    <LocationOutline style="font-size: 14px; vertical-align: -2px" />
                    {{ hotel.address || '暂无地址信息' }}
                  </p>
                  <p class="card-h-desc" v-if="hotel.description">
                    {{ hotel.description }}
                  </p>
                </div>
                <div class="card-h-bottom">
                  <div class="card-h-rating" v-if="hotel.rating">
                    <NRate :value="hotel.rating" allow-half readonly size="small" />
                    <span class="rating-text">{{ hotel.rating?.toFixed(1) }}分</span>
                  </div>
                  <div class="card-h-price">
                    <span class="price">¥{{ hotel.minPrice || hotel.price || '--' }}</span>
                    <span class="price-suffix">起/晚</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <NEmpty v-else-if="!loading" description="暂无搜索结果" style="padding: 80px 0" />
        </NSpin>

        <div class="pagination-wrap" v-if="total > size">
          <NPagination
            v-model:page="page"
            :page-count="Math.ceil(total / size)"
            :page-slot="7"
          />
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.search-section {
  background: #fff;
  padding: 20px 0;
  border-bottom: 1px solid var(--color-border);
  position: sticky;
  top: 64px;
  z-index: 50;
}

.search-bar {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filter-panel {
  margin-top: 16px;
  padding: 16px 20px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
}

.filter-row {
  display: flex;
  gap: 40px;
  flex-wrap: wrap;
}

.filter-item label {
  display: block;
  font-size: 0.8rem;
  color: var(--color-text-medium);
  margin-bottom: 8px;
  font-weight: 500;
}

.star-btns {
  display: flex;
  gap: 8px;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.slide-enter-active, .slide-leave-active {
  transition: all 0.25s ease;
}
.slide-enter-from, .slide-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* Results */
.results-section {
  padding: 32px 0;
}

.results-header {
  margin-bottom: 20px;
}

.results-count {
  font-size: 0.9rem;
  color: var(--color-text-medium);
}

.hotel-grid {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.hotel-card-h {
  display: flex;
  background: #fff;
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.hotel-card-h:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.card-h-image {
  width: 280px;
  min-height: 200px;
  flex-shrink: 0;
  overflow: hidden;
}

.card-h-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}

.hotel-card-h:hover .card-h-image img {
  transform: scale(1.05);
}

.card-h-body {
  flex: 1;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card-h-title {
  font-size: 1.15rem;
  font-weight: 600;
  color: var(--color-text-dark);
  margin-bottom: 8px;
}

.card-h-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
}

.card-h-addr {
  font-size: 0.85rem;
  color: var(--color-text-medium);
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-h-desc {
  font-size: 0.85rem;
  color: var(--color-text-medium);
  margin-top: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-h-bottom {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: 12px;
}

.card-h-rating {
  display: flex;
  align-items: center;
  gap: 6px;
}

.rating-text {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--color-star);
}

.card-h-price {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.card-h-price .price {
  font-size: 1.5rem;
}

.price-suffix {
  font-size: 0.8rem;
  color: var(--color-text-medium);
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 40px 0 20px;
}

@media (max-width: 768px) {
  .hotel-card-h {
    flex-direction: column;
  }
  .card-h-image {
    width: 100%;
    height: 200px;
  }
  .search-bar {
    flex-wrap: wrap;
  }
}
</style>
