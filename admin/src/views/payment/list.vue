<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getPaymentList } from "@/api/payment";

defineOptions({ name: "PaymentList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const queryParams = reactive({
  page: 1,
  size: 10,
  paymentNo: "",
  status: ""
});

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: "待支付", type: "info" },
  1: { label: "支付成功", type: "success" },
  2: { label: "支付失败", type: "danger" },
  3: { label: "已退款", type: "warning" }
};

const statusOptions = [
  { label: "全部", value: "" },
  { label: "待支付", value: 0 },
  { label: "支付成功", value: 1 },
  { label: "支付失败", value: 2 },
  { label: "已退款", value: 3 }
];

async function fetchData() {
  loading.value = true;
  try {
    const res = await getPaymentList(queryParams);
    if (res.code === 200) {
      tableData.value = res.data.records;
      total.value = res.data.total;
    }
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  queryParams.page = 1;
  fetchData();
}

function resetQuery() {
  queryParams.paymentNo = "";
  queryParams.status = "";
  handleSearch();
}

function handleSizeChange(val: number) {
  queryParams.size = val;
  fetchData();
}

function handleCurrentChange(val: number) {
  queryParams.page = val;
  fetchData();
}

onMounted(() => fetchData());
</script>

<template>
  <div class="main">
    <el-card shadow="never" class="mb-4">
      <el-form :model="queryParams" inline>
        <el-form-item label="支付单号">
          <el-input
            v-model="queryParams.paymentNo"
            placeholder="请输入支付单号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" clearable placeholder="全部">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <span class="text-lg font-bold">支付记录</span>
      </template>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="paymentNo" label="支付单号" min-width="200" />
        <el-table-column prop="orderNo" label="关联订单号" min-width="180" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="payMethod" label="支付方式" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="(statusMap[row.status]?.type as any) || 'info'">
              {{ statusMap[row.status]?.label || "未知" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="支付时间" width="170" />
      </el-table>

      <div class="mt-4 flex justify-end">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.mb-4 {
  margin-bottom: 16px;
}
.mt-4 {
  margin-top: 16px;
}
.flex {
  display: flex;
}
.justify-end {
  justify-content: flex-end;
}
.text-lg {
  font-size: 18px;
}
.font-bold {
  font-weight: bold;
}
</style>
