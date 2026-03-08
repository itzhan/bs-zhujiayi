<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import {
  getOrderList,
  updateOrderStatus,
  checkIn,
  checkOut,
  processRefund
} from "@/api/order";
import { ElMessage, ElMessageBox } from "element-plus";

defineOptions({ name: "OrderList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const queryParams = reactive({
  page: 1,
  size: 10,
  orderNo: "",
  status: "",
  keyword: ""
});

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: "待支付", type: "info" },
  1: { label: "已支付", type: "primary" },
  2: { label: "已入住", type: "success" },
  3: { label: "已退房", type: "" },
  4: { label: "已取消", type: "warning" },
  5: { label: "已退款", type: "danger" }
};

const statusOptions = [
  { label: "全部", value: "" },
  { label: "待支付", value: 0 },
  { label: "已支付", value: 1 },
  { label: "已入住", value: 2 },
  { label: "已退房", value: 3 },
  { label: "已取消", value: 4 },
  { label: "已退款", value: 5 }
];

const refundDialogVisible = ref(false);
const refundForm = reactive({
  orderId: null as number | null,
  reason: "",
  amount: 0
});

async function fetchData() {
  loading.value = true;
  try {
    const res = await getOrderList(queryParams);
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
  queryParams.orderNo = "";
  queryParams.status = "";
  queryParams.keyword = "";
  handleSearch();
}

async function handleCheckIn(row: any) {
  await ElMessageBox.confirm("确认办理入住？", "提示", { type: "info" });
  await checkIn(row.id);
  ElMessage.success("入住成功");
  fetchData();
}

async function handleCheckOut(row: any) {
  await ElMessageBox.confirm("确认办理退房？", "提示", { type: "info" });
  await checkOut(row.id);
  ElMessage.success("退房成功");
  fetchData();
}

function handleRefund(row: any) {
  refundForm.orderId = row.id;
  refundForm.reason = "";
  refundForm.amount = row.totalAmount;
  refundDialogVisible.value = true;
}

async function submitRefund() {
  if (!refundForm.orderId) return;
  await processRefund(refundForm.orderId, {
    reason: refundForm.reason,
    amount: refundForm.amount
  });
  ElMessage.success("退款处理成功");
  refundDialogVisible.value = false;
  fetchData();
}

async function handleCancel(row: any) {
  await ElMessageBox.confirm("确认取消该订单？", "提示", {
    type: "warning"
  });
  await updateOrderStatus(row.id, 4);
  ElMessage.success("订单已取消");
  fetchData();
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
        <el-form-item label="订单号">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="客户姓名/酒店名"
            clearable
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
        <span class="text-lg font-bold">订单列表</span>
      </template>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="username" label="客户" width="100" />
        <el-table-column prop="hotelName" label="酒店" min-width="150" />
        <el-table-column prop="roomTypeName" label="房型" width="120" />
        <el-table-column prop="checkInDate" label="入住日期" width="110" />
        <el-table-column prop="checkOutDate" label="退房日期" width="110" />
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="(statusMap[row.status]?.type as any) || 'info'">
              {{ statusMap[row.status]?.label || "未知" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              link
              type="primary"
              @click="handleCheckIn(row)"
            >
              入住
            </el-button>
            <el-button
              v-if="row.status === 2"
              link
              type="primary"
              @click="handleCheckOut(row)"
            >
              退房
            </el-button>
            <el-button
              v-if="row.status === 1"
              link
              type="warning"
              @click="handleRefund(row)"
            >
              退款
            </el-button>
            <el-button
              v-if="row.status === 0"
              link
              type="danger"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
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

    <!-- 退款弹窗 -->
    <el-dialog v-model="refundDialogVisible" title="处理退款" width="500px">
      <el-form label-width="100px">
        <el-form-item label="退款金额">
          <el-input-number v-model="refundForm.amount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="退款原因">
          <el-input
            v-model="refundForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入退款原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRefund">确认退款</el-button>
      </template>
    </el-dialog>
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
