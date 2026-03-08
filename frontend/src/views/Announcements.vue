<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NCard, NTag, NEmpty, NSpin, NTimeline, NTimelineItem } from 'naive-ui'
import { MegaphoneOutline } from '@vicons/ionicons5'
import { getAnnouncements } from '@/api/user'

const announcements = ref<any[]>([])
const loading = ref(true)

async function loadAnnouncements() {
  try {
    const res: any = await getAnnouncements()
    announcements.value = Array.isArray(res.data) ? res.data : res.data?.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(loadAnnouncements)
</script>

<template>
  <div class="announcements-page">
    <div class="container" style="padding-top: 32px; padding-bottom: 60px">
      <h1 class="page-title">
        <MegaphoneOutline style="font-size: 28px; vertical-align: -5px; margin-right: 8px" />
        公告资讯
      </h1>

      <NSpin :show="loading">
        <div v-if="announcements.length" class="announcements-list">
          <NCard
            v-for="item in announcements"
            :key="item.id"
            class="announcement-card"
            :bordered="true"
            size="medium"
          >
            <div class="announcement-header">
              <h3>{{ item.title }}</h3>
              <NTag size="small" :bordered="false" type="info">
                {{ item.type || '公告' }}
              </NTag>
            </div>
            <p class="announcement-content">{{ item.content }}</p>
            <div class="announcement-meta">
              <span>{{ item.createTime || item.publishTime || '' }}</span>
            </div>
          </NCard>
        </div>
        <NEmpty v-else-if="!loading" description="暂无公告" style="padding: 80px 0" />
      </NSpin>
    </div>
  </div>
</template>

<style scoped>
.page-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 28px;
  color: var(--color-text-dark);
}

.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.announcement-card {
  transition: box-shadow 0.2s;
}

.announcement-card:hover {
  box-shadow: var(--shadow-md);
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.announcement-header h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--color-text-dark);
}

.announcement-content {
  font-size: 0.9rem;
  color: var(--color-text-medium);
  line-height: 1.8;
  white-space: pre-wrap;
}

.announcement-meta {
  margin-top: 12px;
  font-size: 0.8rem;
  color: var(--color-text-light);
}
</style>
