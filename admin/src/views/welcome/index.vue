<script setup lang="ts">
import { ref, onMounted } from "vue";
import {
  getDashboardStats,
  getHotelRanking
} from "@/api/statistics";

defineOptions({ name: "Welcome" });

const stats = ref({
  totalUsers: 0,
  totalHotels: 0,
  totalOrders: 0,
  totalRevenue: 0,
  todayOrders: 0,
  todayRevenue: 0,
  monthOrders: 0,
  monthRevenue: 0
});
const rankingData = ref([]);
const loading = ref(false);

const statCards = [
  {
    key: "totalUsers",
    title: "总用户数",
    icon: "User",
    color: "#409eff",
    bgColor: "#ecf5ff"
  },
  {
    key: "totalHotels",
    title: "酒店总数",
    icon: "OfficeBuilding",
    color: "#67c23a",
    bgColor: "#f0f9eb"
  },
  {
    key: "totalOrders",
    title: "订单总数",
    icon: "Document",
    color: "#e6a23c",
    bgColor: "#fdf6ec"
  },
  {
    key: "totalRevenue",
    title: "总营收(元)",
    icon: "Wallet",
    color: "#f56c6c",
    bgColor: "#fef0f0"
  }
];

async function fetchDashboard() {
  loading.value = true;
  try {
    const res = await getDashboardStats();
    if (res.code === 200 && res.data) {
      stats.value = { ...stats.value, ...res.data };
    }
  } catch {
    // 接口未就绪时使用默认值
  } finally {
    loading.value = false;
  }
}

async function fetchRanking() {
  try {
    const res = await getHotelRanking();
    if (res.code === 200 && res.data) {
      rankingData.value = res.data;
    }
  } catch {
    // 接口未就绪时使用默认值
  }
}

function formatNumber(val: number) {
  if (val >= 10000) {
    return (val / 10000).toFixed(1) + "万";
  }
  return val?.toLocaleString() || "0";
}

onMounted(() => {
  fetchDashboard();
  fetchRanking();
});
</script>

<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb-20">
      <el-col v-for="card in statCards" :key="card.key" :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-info">
              <div class="stat-title">{{ card.title }}</div>
              <div class="stat-value">
                {{ card.key === "totalRevenue" ? "¥" : "" }}{{ formatNumber(stats[card.key]) }}
              </div>
            </div>
            <div
              class="stat-icon"
              :style="{ backgroundColor: card.bgColor, color: card.color }"
            >
              <el-icon :size="28">
                <component :is="card.icon" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 今日/本月数据 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :xs="24" :sm="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">今日数据</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-statistic title="今日订单" :value="stats.todayOrders" />
            </el-col>
            <el-col :span="12">
              <el-statistic title="今日营收" :value="stats.todayRevenue" prefix="¥" />
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">本月数据</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-statistic title="本月订单" :value="stats.monthOrders" />
            </el-col>
            <el-col :span="12">
              <el-statistic title="本月营收" :value="stats.monthRevenue" prefix="¥" />
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 + 酒店排名 -->
    <el-row :gutter="20">
      <el-col :xs="24" :lg="14">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">订单/营收趋势</span>
          </template>
          <div class="chart-placeholder">
            <el-empty description="图表区域（可接入 ECharts）" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">酒店排名 TOP 10</span>
          </template>
          <el-table :data="rankingData" stripe size="small" :max-height="380">
            <el-table-column type="index" label="排名" width="60">
              <template #default="{ $index }">
                <el-tag
                  v-if="$index < 3"
                  :type="$index === 0 ? 'danger' : $index === 1 ? 'warning' : 'success'"
                  size="small"
                  round
                >
                  {{ $index + 1 }}
                </el-tag>
                <span v-else>{{ $index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="hotelName" label="酒店名称" min-width="140" />
            <el-table-column prop="orderCount" label="订单数" width="80" />
            <el-table-column prop="revenue" label="营收" width="100">
              <template #default="{ row }">¥{{ row.revenue?.toLocaleString() || 0 }}</template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!rankingData.length" description="暂无排名数据" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 4px;
}

.mb-20 {
  margin-bottom: 20px;
}

.stat-card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-placeholder {
  height: 380px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
