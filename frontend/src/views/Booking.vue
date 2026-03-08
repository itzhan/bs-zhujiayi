<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NButton, NCard, NDatePicker, NInput, NInputNumber, NForm, NFormItem,
  NSelect, NDivider, NAlert, NTag, useMessage, NRadioGroup, NRadio, NSpin
} from 'naive-ui'
import { createOrder } from '@/api/order'
import { payOrder } from '@/api/payment'
import { getRoomInventory, getRoomTypeDetail } from '@/api/hotel'
import { getMyCoupons } from '@/api/coupon'

const route = useRoute()
const router = useRouter()
const message = useMessage()

const hotelId = Number(route.query.hotelId)
const roomTypeId = Number(route.query.roomTypeId)
const hotelName = route.query.hotelName as string || ''
const roomName = route.query.roomName as string || ''
const unitPrice = Number(route.query.price) || 0

const roomDetail = ref<any>({})
const loading = ref(false)
const submitting = ref(false)

const dateRange = ref<[number, number] | null>(null)
const guestName = ref('')
const guestPhone = ref('')
const roomCount = ref(1)
const remark = ref('')
const selectedCouponId = ref<number | null>(null)
const paymentMethod = ref('ALIPAY')
const coupons = ref<any[]>([])

const nights = computed(() => {
  if (!dateRange.value) return 0
  const diff = dateRange.value[1] - dateRange.value[0]
  return Math.ceil(diff / (1000 * 60 * 60 * 24))
})

const totalPrice = computed(() => {
  const base = (roomDetail.value.price || unitPrice) * nights.value * roomCount.value
  const coupon = coupons.value.find((c: any) => c.id === selectedCouponId.value)
  if (coupon) {
    if (coupon.type === 'FIXED' || coupon.discountType === 'FIXED') {
      return Math.max(0, base - (coupon.discountAmount || coupon.amount || 0))
    }
    if (coupon.type === 'PERCENT' || coupon.discountType === 'PERCENT') {
      return base * (1 - (coupon.discountRate || coupon.discount || 0) / 100)
    }
  }
  return base
})

const formatDate = (ts: number) => {
  const d = new Date(ts)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const couponOptions = computed(() => {
  return [
    { label: '不使用优惠券', value: null },
    ...coupons.value.map((c: any) => ({
      label: c.name || c.title || `优惠券 (${c.discountAmount || c.discountRate || ''})`,
      value: c.id
    }))
  ]
})

const paymentOptions = [
  { label: '支付宝', value: 'ALIPAY' },
  { label: '微信支付', value: 'WECHAT' },
  { label: '银行卡', value: 'BANK_CARD' }
]

async function loadRoomDetail() {
  if (!roomTypeId) return
  try {
    const res: any = await getRoomTypeDetail(roomTypeId)
    roomDetail.value = res.data || {}
  } catch (e) {
    console.error(e)
  }
}

async function loadCoupons() {
  try {
    const res: any = await getMyCoupons({ status: 'AVAILABLE' })
    coupons.value = Array.isArray(res.data) ? res.data : res.data?.records || []
  } catch (e) {
    console.error(e)
  }
}

async function handleSubmit() {
  if (!dateRange.value) {
    message.warning('请选择入住和退房日期')
    return
  }
  if (!guestName.value.trim()) {
    message.warning('请输入入住人姓名')
    return
  }
  if (!guestPhone.value.trim()) {
    message.warning('请输入联系电话')
    return
  }

  submitting.value = true
  try {
    const orderData: any = {
      hotelId,
      roomTypeId,
      checkInDate: formatDate(dateRange.value[0]),
      checkOutDate: formatDate(dateRange.value[1]),
      guestName: guestName.value,
      guestPhone: guestPhone.value,
      roomCount: roomCount.value,
      remark: remark.value
    }
    if (selectedCouponId.value) {
      orderData.couponId = selectedCouponId.value
    }

    const res: any = await createOrder(orderData)
    const orderId = res.data?.id || res.data

    // Auto pay
    try {
      await payOrder({ orderId, paymentMethod: paymentMethod.value })
      message.success('预订成功，支付完成！')
    } catch (e) {
      message.success('预订成功，请前往订单页面完成支付')
    }

    router.push('/user/orders')
  } catch (e: any) {
    message.error(e.message || '预订失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadRoomDetail()
  loadCoupons()
})
</script>

<template>
  <div class="booking-page">
    <div class="container" style="padding-top: 32px; padding-bottom: 60px">
      <h1 class="page-title">确认预订</h1>

      <div class="booking-layout">
        <!-- Left: Form -->
        <div class="booking-form">
          <NCard title="酒店信息" :bordered="true" size="medium">
            <div class="hotel-brief">
              <h3>{{ hotelName }}</h3>
              <p>{{ roomDetail.name || roomName }}</p>
              <div class="brief-tags">
                <NTag v-if="roomDetail.bedType" size="small" :bordered="false">{{ roomDetail.bedType }}</NTag>
                <NTag v-if="roomDetail.area" size="small" :bordered="false">{{ roomDetail.area }}m²</NTag>
                <NTag v-if="roomDetail.maxGuests" size="small" :bordered="false">{{ roomDetail.maxGuests }}人入住</NTag>
              </div>
            </div>
          </NCard>

          <NCard title="入住信息" :bordered="true" size="medium" style="margin-top: 20px">
            <NForm label-placement="top">
              <NFormItem label="入住/退房日期" required>
                <NDatePicker
                  v-model:value="dateRange"
                  type="daterange"
                  :is-date-disabled="(ts: number) => ts < Date.now() - 86400000"
                  clearable
                  style="width: 100%"
                />
              </NFormItem>
              <div style="display: flex; gap: 16px">
                <NFormItem label="入住人姓名" required style="flex: 1">
                  <NInput v-model:value="guestName" placeholder="请输入姓名" />
                </NFormItem>
                <NFormItem label="联系电话" required style="flex: 1">
                  <NInput v-model:value="guestPhone" placeholder="请输入手机号" />
                </NFormItem>
              </div>
              <NFormItem label="房间数量">
                <NInputNumber v-model:value="roomCount" :min="1" :max="10" style="width: 150px" />
              </NFormItem>
              <NFormItem label="备注">
                <NInput v-model:value="remark" type="textarea" placeholder="特殊需求（选填）" :rows="3" />
              </NFormItem>
            </NForm>
          </NCard>

          <NCard title="优惠券" :bordered="true" size="medium" style="margin-top: 20px" v-if="coupons.length">
            <NSelect
              v-model:value="selectedCouponId"
              :options="couponOptions"
              placeholder="选择优惠券"
            />
          </NCard>

          <NCard title="支付方式" :bordered="true" size="medium" style="margin-top: 20px">
            <NRadioGroup v-model:value="paymentMethod">
              <NRadio v-for="opt in paymentOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </NRadio>
            </NRadioGroup>
          </NCard>
        </div>

        <!-- Right: Summary -->
        <div class="booking-summary">
          <NCard :bordered="true" size="medium">
            <h3 class="summary-title">费用明细</h3>
            <div class="summary-rows">
              <div class="summary-row">
                <span>房间单价</span>
                <span class="price">¥{{ roomDetail.price || unitPrice }}</span>
              </div>
              <div class="summary-row">
                <span>入住天数</span>
                <span>{{ nights || '--' }} 晚</span>
              </div>
              <div class="summary-row">
                <span>房间数量</span>
                <span>× {{ roomCount }}</span>
              </div>
              <div class="summary-row" v-if="selectedCouponId">
                <span>优惠券</span>
                <span style="color: #10B981">已使用</span>
              </div>
              <NDivider style="margin: 12px 0" />
              <div class="summary-row total">
                <span>合计</span>
                <span class="price-large">¥{{ totalPrice > 0 ? totalPrice.toFixed(2) : '--' }}</span>
              </div>
            </div>

            <NButton
              type="primary"
              block
              size="large"
              :loading="submitting"
              :disabled="!dateRange || !guestName || !guestPhone"
              style="background: #E07A5F; border-color: #E07A5F; margin-top: 20px"
              @click="handleSubmit"
            >
              确认预订并支付
            </NButton>

            <NAlert type="info" :bordered="false" style="margin-top: 16px">
              预订后可在"我的订单"中查看详情和申请退款
            </NAlert>
          </NCard>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 24px;
}

.booking-layout {
  display: flex;
  gap: 24px;
}

.booking-form {
  flex: 1;
}

.booking-summary {
  width: 340px;
  flex-shrink: 0;
}

.booking-summary .n-card {
  position: sticky;
  top: 96px;
}

.hotel-brief h3 {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 4px;
}

.hotel-brief p {
  color: var(--color-text-medium);
  font-size: 0.9rem;
  margin-bottom: 8px;
}

.brief-tags {
  display: flex;
  gap: 6px;
}

.summary-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 16px;
}

.summary-rows {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 0.9rem;
  color: var(--color-text-medium);
}

.summary-row.total {
  font-size: 1rem;
  font-weight: 600;
  color: var(--color-text-dark);
}

@media (max-width: 768px) {
  .booking-layout {
    flex-direction: column;
  }
  .booking-summary {
    width: 100%;
  }
}
</style>
