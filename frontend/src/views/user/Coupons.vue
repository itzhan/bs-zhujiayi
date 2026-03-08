<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  NCard, NButton, NTag, NTabs, NTabPane, NEmpty, NSpin, useMessage
} from 'naive-ui'
import { TicketOutline, GiftOutline } from '@vicons/ionicons5'
import { getAvailableCoupons, receiveCoupon, getMyCoupons } from '@/api/coupon'

const message = useMessage()

const activeTab = ref('available')
const availableCoupons = ref<any[]>([])
const myCoupons = ref<any[]>([])
const loading = ref(false)
const myStatus = ref('')

async function loadAvailableCoupons() {
  loading.value = true
  try {
    const res: any = await getAvailableCoupons()
    availableCoupons.value = Array.isArray(res.data) ? res.data : res.data?.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function loadMyCoupons() {
  loading.value = true
  try {
    const res: any = await getMyCoupons({ status: myStatus.value || undefined })
    myCoupons.value = Array.isArray(res.data) ? res.data : res.data?.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleReceive(id: number) {
  try {
    await receiveCoupon(id)
    message.success('领取成功！')
    loadAvailableCoupons()
    loadMyCoupons()
  } catch (e: any) {
    message.error(e.message || '领取失败')
  }
}

function formatDiscount(coupon: any) {
  if (coupon.type === 'FIXED' || coupon.discountType === 'FIXED') {
    return `¥${coupon.discountAmount || coupon.amount || 0}`
  }
  if (coupon.type === 'PERCENT' || coupon.discountType === 'PERCENT') {
    return `${coupon.discountRate || coupon.discount || 0}%`
  }
  return coupon.discountAmount || coupon.amount || '--'
}

function handleTabChange(val: string) {
  activeTab.value = val
  if (val === 'available') {
    loadAvailableCoupons()
  } else {
    loadMyCoupons()
  }
}

onMounted(() => {
  loadAvailableCoupons()
  loadMyCoupons()
})
</script>

<template>
  <div class="coupons-page">
    <div class="container" style="padding-top: 32px; padding-bottom: 60px">
      <h1 class="page-title">
        <TicketOutline style="font-size: 26px; vertical-align: -4px; margin-right: 8px" />
        优惠券
      </h1>

      <NTabs type="line" :value="activeTab" @update:value="handleTabChange">
        <NTabPane name="available" tab="可领取">
          <NSpin :show="loading">
            <div v-if="availableCoupons.length" class="coupon-grid">
              <div v-for="coupon in availableCoupons" :key="coupon.id" class="coupon-card available">
                <div class="coupon-left">
                  <div class="coupon-value price">{{ formatDiscount(coupon) }}</div>
                  <div class="coupon-condition" v-if="coupon.minAmount">
                    满¥{{ coupon.minAmount }}可用
                  </div>
                </div>
                <div class="coupon-right">
                  <h3>{{ coupon.name || coupon.title || '优惠券' }}</h3>
                  <p v-if="coupon.description">{{ coupon.description }}</p>
                  <p class="coupon-expire" v-if="coupon.expireTime || coupon.endTime">
                    有效期至 {{ coupon.expireTime || coupon.endTime }}
                  </p>
                  <NButton
                    type="primary"
                    size="small"
                    style="background: #E07A5F; border-color: #E07A5F; margin-top: 8px"
                    @click="handleReceive(coupon.id)"
                  >
                    <template #icon>
                      <GiftOutline />
                    </template>
                    立即领取
                  </NButton>
                </div>
              </div>
            </div>
            <NEmpty v-else-if="!loading" description="暂无可领取的优惠券" style="padding: 60px 0" />
          </NSpin>
        </NTabPane>

        <NTabPane name="mine" tab="我的优惠券">
          <NSpin :show="loading">
            <div v-if="myCoupons.length" class="coupon-grid">
              <div
                v-for="coupon in myCoupons"
                :key="coupon.id"
                :class="['coupon-card', coupon.status === 'USED' || coupon.status === 'EXPIRED' ? 'used' : '']"
              >
                <div class="coupon-left">
                  <div class="coupon-value" :class="{ price: coupon.status !== 'USED' && coupon.status !== 'EXPIRED' }">
                    {{ formatDiscount(coupon) }}
                  </div>
                  <div class="coupon-condition" v-if="coupon.minAmount">
                    满¥{{ coupon.minAmount }}可用
                  </div>
                </div>
                <div class="coupon-right">
                  <div class="coupon-name-row">
                    <h3>{{ coupon.name || coupon.title || '优惠券' }}</h3>
                    <NTag
                      size="small"
                      :type="coupon.status === 'AVAILABLE' ? 'success' : coupon.status === 'USED' ? 'default' : 'warning'"
                    >
                      {{ coupon.status === 'AVAILABLE' ? '可用' : coupon.status === 'USED' ? '已使用' : '已过期' }}
                    </NTag>
                  </div>
                  <p class="coupon-expire" v-if="coupon.expireTime || coupon.endTime">
                    有效期至 {{ coupon.expireTime || coupon.endTime }}
                  </p>
                </div>
              </div>
            </div>
            <NEmpty v-else-if="!loading" description="暂无优惠券" style="padding: 60px 0" />
          </NSpin>
        </NTabPane>
      </NTabs>
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

.coupon-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-top: 16px;
}

.coupon-card {
  display: flex;
  background: #fff;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
  transition: box-shadow 0.2s;
}

.coupon-card:hover {
  box-shadow: var(--shadow-md);
}

.coupon-card.used {
  opacity: 0.6;
}

.coupon-left {
  width: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 16px;
  background: rgba(224, 122, 95, 0.06);
  border-right: 2px dashed var(--color-border);
}

.coupon-value {
  font-size: 1.5rem;
  font-weight: 800;
}

.coupon-condition {
  font-size: 0.75rem;
  color: var(--color-text-medium);
  margin-top: 4px;
}

.coupon-right {
  flex: 1;
  padding: 16px 20px;
}

.coupon-name-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.coupon-right h3 {
  font-size: 0.95rem;
  font-weight: 600;
  margin-bottom: 4px;
}

.coupon-right p {
  font-size: 0.8rem;
  color: var(--color-text-medium);
}

.coupon-expire {
  font-size: 0.75rem;
  color: var(--color-text-light);
  margin-top: 4px;
}

@media (max-width: 768px) {
  .coupon-grid {
    grid-template-columns: 1fr;
  }
}
</style>
