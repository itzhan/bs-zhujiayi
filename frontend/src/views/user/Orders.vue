<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  NCard, NButton, NTag, NTabs, NTabPane, NPagination,
  NEmpty, NSpin, NModal, NRate, NInput, useMessage, NPopconfirm, NSelect
} from 'naive-ui'
import { getMyOrders, cancelOrder, requestRefund } from '@/api/order'
import { payOrder } from '@/api/payment'
import { createReview } from '@/api/review'

const router = useRouter()
const message = useMessage()

const orders = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const status = ref('')
const loading = ref(false)

// Review modal
const showReview = ref(false)
const reviewOrder = ref<any>(null)
const reviewForm = ref({ rating: 5, content: '' })
const reviewLoading = ref(false)

// Pay modal
const showPay = ref(false)
const payOrderId = ref<number | null>(null)
const paymentMethod = ref('ALIPAY')

const statusMap: Record<string, { label: string; type: 'default' | 'info' | 'success' | 'warning' | 'error' }> = {
  PENDING: { label: '待支付', type: 'warning' },
  PAID: { label: '已支付', type: 'info' },
  CONFIRMED: { label: '已确认', type: 'info' },
  CHECKED_IN: { label: '已入住', type: 'success' },
  COMPLETED: { label: '已完成', type: 'success' },
  CANCELLED: { label: '已取消', type: 'default' },
  REFUNDING: { label: '退款中', type: 'warning' },
  REFUNDED: { label: '已退款', type: 'default' }
}

const tabList = [
  { label: '全部订单', value: '' },
  { label: '待支付', value: 'PENDING' },
  { label: '已支付', value: 'PAID' },
  { label: '已确认', value: 'CONFIRMED' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' }
]

async function loadOrders() {
  loading.value = true
  try {
    const params: any = { page: page.value, size: size.value }
    if (status.value) params.status = status.value
    const res: any = await getMyOrders(params)
    orders.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleCancel(id: number) {
  try {
    await cancelOrder(id)
    message.success('订单已取消')
    loadOrders()
  } catch (e: any) {
    message.error(e.message || '取消失败')
  }
}

async function handleRefund(id: number) {
  try {
    await requestRefund(id)
    message.success('退款申请已提交')
    loadOrders()
  } catch (e: any) {
    message.error(e.message || '申请退款失败')
  }
}

function openPay(orderId: number) {
  payOrderId.value = orderId
  showPay.value = true
}

async function handlePay() {
  if (!payOrderId.value) return
  try {
    await payOrder({ orderId: payOrderId.value, paymentMethod: paymentMethod.value })
    message.success('支付成功')
    showPay.value = false
    loadOrders()
  } catch (e: any) {
    message.error(e.message || '支付失败')
  }
}

function openReview(order: any) {
  reviewOrder.value = order
  reviewForm.value = { rating: 5, content: '' }
  showReview.value = true
}

async function submitReview() {
  if (!reviewForm.value.content.trim()) {
    message.warning('请输入评价内容')
    return
  }
  reviewLoading.value = true
  try {
    await createReview({
      orderId: reviewOrder.value.id,
      hotelId: reviewOrder.value.hotelId,
      roomTypeId: reviewOrder.value.roomTypeId,
      rating: reviewForm.value.rating,
      content: reviewForm.value.content
    })
    message.success('评价已提交')
    showReview.value = false
    loadOrders()
  } catch (e: any) {
    message.error(e.message || '评价失败')
  } finally {
    reviewLoading.value = false
  }
}

function handleTabChange(val: string) {
  status.value = val
  page.value = 1
  loadOrders()
}

onMounted(loadOrders)
</script>

<template>
  <div class="orders-page">
    <div class="container" style="padding-top: 32px; padding-bottom: 60px">
      <h1 class="page-title">我的订单</h1>

      <NTabs type="line" :value="status" @update:value="handleTabChange">
        <NTabPane v-for="tab in tabList" :key="tab.value" :name="tab.value" :tab="tab.label" />
      </NTabs>

      <NSpin :show="loading">
        <div class="order-list" v-if="orders.length">
          <NCard v-for="order in orders" :key="order.id" :bordered="true" size="medium" class="order-card">
            <div class="order-header">
              <div class="order-id">
                <span class="label">订单号：</span>
                <span>{{ order.orderNo || order.id }}</span>
              </div>
              <NTag
                :type="statusMap[order.status]?.type || 'default'"
                size="small"
              >
                {{ statusMap[order.status]?.label || order.status }}
              </NTag>
            </div>

            <div class="order-body">
              <div class="order-info">
                <h3>{{ order.hotelName || '酒店' }} · {{ order.roomTypeName || order.roomName || '房型' }}</h3>
                <div class="order-details">
                  <span v-if="order.checkInDate">入住：{{ order.checkInDate }}</span>
                  <span v-if="order.checkOutDate">退房：{{ order.checkOutDate }}</span>
                  <span v-if="order.roomCount">{{ order.roomCount }}间</span>
                  <span v-if="order.guestName">{{ order.guestName }}</span>
                </div>
              </div>
              <div class="order-price">
                <span class="price-large">¥{{ order.totalAmount || order.totalPrice || '--' }}</span>
              </div>
            </div>

            <div class="order-actions">
              <span class="order-time">{{ order.createTime || order.createdAt || '' }}</span>
              <div class="action-btns">
                <NButton
                  v-if="order.status === 'PENDING'"
                  type="primary"
                  size="small"
                  style="background: #E07A5F; border-color: #E07A5F"
                  @click="openPay(order.id)"
                >
                  去支付
                </NButton>
                <NPopconfirm
                  v-if="order.status === 'PENDING'"
                  @positive-click="handleCancel(order.id)"
                >
                  <template #trigger>
                    <NButton size="small" quaternary>取消订单</NButton>
                  </template>
                  确认取消此订单？
                </NPopconfirm>
                <NPopconfirm
                  v-if="order.status === 'PAID' || order.status === 'CONFIRMED'"
                  @positive-click="handleRefund(order.id)"
                >
                  <template #trigger>
                    <NButton size="small" quaternary type="warning">申请退款</NButton>
                  </template>
                  确认申请退款？
                </NPopconfirm>
                <NButton
                  v-if="order.status === 'COMPLETED' && !order.reviewed"
                  size="small"
                  type="primary"
                  ghost
                  @click="openReview(order)"
                >
                  评价
                </NButton>
              </div>
            </div>
          </NCard>
        </div>
        <NEmpty v-else-if="!loading" description="暂无订单" style="padding: 80px 0" />
      </NSpin>

      <div class="pagination-wrap" v-if="total > size">
        <NPagination
          v-model:page="page"
          :page-count="Math.ceil(total / size)"
          @update:page="loadOrders"
        />
      </div>
    </div>

    <!-- Pay Modal -->
    <NModal v-model:show="showPay" preset="card" title="选择支付方式" style="width: 420px">
      <NSelect
        v-model:value="paymentMethod"
        :options="[
          { label: '支付宝', value: 'ALIPAY' },
          { label: '微信支付', value: 'WECHAT' },
          { label: '银行卡', value: 'BANK_CARD' }
        ]"
        style="margin-bottom: 16px"
      />
      <NButton type="primary" block @click="handlePay" style="background: #E07A5F; border-color: #E07A5F">
        确认支付
      </NButton>
    </NModal>

    <!-- Review Modal -->
    <NModal v-model:show="showReview" preset="card" title="评价订单" style="width: 500px">
      <div style="margin-bottom: 16px">
        <p style="margin-bottom: 8px; font-weight: 500">评分</p>
        <NRate v-model:value="reviewForm.rating" allow-half />
      </div>
      <div style="margin-bottom: 16px">
        <p style="margin-bottom: 8px; font-weight: 500">评价内容</p>
        <NInput
          v-model:value="reviewForm.content"
          type="textarea"
          :rows="4"
          placeholder="分享您的入住体验..."
        />
      </div>
      <NButton
        type="primary"
        block
        :loading="reviewLoading"
        @click="submitReview"
      >
        提交评价
      </NButton>
    </NModal>
  </div>
</template>

<style scoped>
.page-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 24px;
  color: var(--color-text-dark);
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.order-card {
  transition: box-shadow 0.2s;
}

.order-card:hover {
  box-shadow: var(--shadow-md);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-id {
  font-size: 0.85rem;
  color: var(--color-text-medium);
}

.order-id .label {
  color: var(--color-text-light);
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.order-info h3 {
  font-size: 1.05rem;
  font-weight: 600;
  margin-bottom: 6px;
}

.order-details {
  display: flex;
  gap: 16px;
  font-size: 0.85rem;
  color: var(--color-text-medium);
}

.order-price {
  flex-shrink: 0;
}

.order-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}

.order-time {
  font-size: 0.8rem;
  color: var(--color-text-light);
}

.action-btns {
  display: flex;
  gap: 8px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 32px 0;
}
</style>
