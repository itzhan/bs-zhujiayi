<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NRate, NTag, NEmpty, NSpin, useMessage, NPopconfirm } from 'naive-ui'
import { LocationOutline, HeartDislikeOutline, StarOutline } from '@vicons/ionicons5'
import { getMyFavorites, toggleFavorite } from '@/api/favorite'

const router = useRouter()
const message = useMessage()

const favorites = ref<any[]>([])
const loading = ref(true)

async function loadFavorites() {
  loading.value = true
  try {
    const res: any = await getMyFavorites()
    favorites.value = Array.isArray(res.data) ? res.data : res.data?.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleRemoveFavorite(hotelId: number) {
  try {
    await toggleFavorite(hotelId)
    favorites.value = favorites.value.filter((f: any) => (f.hotelId || f.id) !== hotelId)
    message.success('已取消收藏')
  } catch (e) {
    message.error('操作失败')
  }
}

function goHotel(item: any) {
  router.push(`/hotels/${item.hotelId || item.id}`)
}

onMounted(loadFavorites)
</script>

<template>
  <div class="favorites-page">
    <div class="container" style="padding-top: 32px; padding-bottom: 60px">
      <h1 class="page-title">我的收藏</h1>

      <NSpin :show="loading">
        <div v-if="favorites.length" class="favorites-grid">
          <div
            v-for="item in favorites"
            :key="item.id"
            class="hotel-card"
            @click="goHotel(item)"
          >
            <div class="card-image">
              <img
                :src="item.coverImage || item.hotelImage || item.image || 'https://picsum.photos/400/260?random=' + item.id"
                :alt="item.hotelName || item.name"
              />
              <div class="card-badge" v-if="item.starRating">
                <StarOutline style="font-size: 12px" />
                {{ item.starRating }}星
              </div>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ item.hotelName || item.name }}</h3>
              <p class="card-location">
                <LocationOutline style="font-size: 14px; vertical-align: -2px" />
                {{ item.city || item.address || '暂无地址' }}
              </p>
              <div class="card-footer">
                <div class="card-rating" v-if="item.rating">
                  <NRate :value="item.rating" allow-half readonly size="small" />
                </div>
                <div class="card-price" v-if="item.minPrice || item.price">
                  <span class="price">¥{{ item.minPrice || item.price }}</span>
                  <span class="price-unit">/晚</span>
                </div>
              </div>
              <NPopconfirm @positive-click.stop="handleRemoveFavorite(item.hotelId || item.id)">
                <template #trigger>
                  <NButton
                    size="tiny"
                    type="error"
                    ghost
                    style="margin-top: 8px"
                    @click.stop
                  >
                    <template #icon>
                      <HeartDislikeOutline />
                    </template>
                    取消收藏
                  </NButton>
                </template>
                确认取消收藏该酒店？
              </NPopconfirm>
            </div>
          </div>
        </div>
        <NEmpty v-else-if="!loading" description="暂无收藏" style="padding: 80px 0">
          <template #extra>
            <NButton type="primary" @click="router.push('/hotels')">去发现好酒店</NButton>
          </template>
        </NEmpty>
      </NSpin>
    </div>
  </div>
</template>

<style scoped>
.page-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 24px;
  color: var(--color-text-dark);
}

.favorites-grid {
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
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-location {
  font-size: 0.8rem;
  color: var(--color-text-medium);
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.card-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-unit {
  font-size: 0.75rem;
  color: var(--color-text-medium);
}

@media (max-width: 768px) {
  .favorites-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
