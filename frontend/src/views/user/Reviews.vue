<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NCard, NRate, NEmpty, NSpin, NPagination, NTag } from 'naive-ui'
import { getMyReviews } from '@/api/review'

const reviews = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(true)

async function loadReviews() {
  loading.value = true
  try {
    const res: any = await getMyReviews({ page: page.value, size: size.value })
    reviews.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(loadReviews)
</script>

<template>
  <div class="reviews-page">
    <div class="container" style="padding-top: 32px; padding-bottom: 60px; max-width: 800px">
      <h1 class="page-title">我的评价</h1>

      <NSpin :show="loading">
        <div v-if="reviews.length" class="review-list">
          <NCard v-for="review in reviews" :key="review.id" :bordered="true" size="medium" class="review-card">
            <div class="review-header">
              <div>
                <h3 class="review-hotel">{{ review.hotelName || '酒店' }}</h3>
                <NTag size="small" :bordered="false" v-if="review.roomTypeName">
                  {{ review.roomTypeName }}
                </NTag>
              </div>
              <NRate :value="review.rating" allow-half readonly size="small" />
            </div>
            <p class="review-content">{{ review.content }}</p>
            <div class="review-footer">
              <span class="review-date">{{ review.createTime || review.createdAt || '' }}</span>
            </div>
          </NCard>
        </div>
        <NEmpty v-else-if="!loading" description="暂无评价" style="padding: 80px 0" />
      </NSpin>

      <div class="pagination-wrap" v-if="total > size">
        <NPagination
          v-model:page="page"
          :page-count="Math.ceil(total / size)"
          @update:page="loadReviews"
        />
      </div>
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

.review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-card {
  transition: box-shadow 0.2s;
}

.review-card:hover {
  box-shadow: var(--shadow-md);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.review-hotel {
  font-size: 1.05rem;
  font-weight: 600;
  margin-bottom: 4px;
}

.review-content {
  font-size: 0.9rem;
  color: var(--color-text-medium);
  line-height: 1.7;
}

.review-footer {
  margin-top: 12px;
  font-size: 0.8rem;
  color: var(--color-text-light);
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 32px 0;
}
</style>
