<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getReviewList, auditReview, deleteReview } from "@/api/review";
import { ElMessage, ElMessageBox } from "element-plus";

defineOptions({ name: "ReviewList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const queryParams = reactive({
  page: 1,
  size: 10,
  status: "",
  hotelId: ""
});

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: "待审核", type: "warning" },
  1: { label: "已通过", type: "success" },
  2: { label: "已拒绝", type: "danger" }
};

const statusOptions = [
  { label: "全部", value: "" },
  { label: "待审核", value: 0 },
  { label: "已通过", value: 1 },
  { label: "已拒绝", value: 2 }
];

async function fetchData() {
  loading.value = true;
  try {
    const res = await getReviewList(queryParams);
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
  queryParams.status = "";
  queryParams.hotelId = "";
  handleSearch();
}

async function handleApprove(row: any) {
  await auditReview(row.id, { status: 1 });
  ElMessage.success("审核通过");
  fetchData();
}

async function handleReject(row: any) {
  await auditReview(row.id, { status: 2 });
  ElMessage.success("已拒绝");
  fetchData();
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm("确认删除该评价？", "提示", {
    type: "warning"
  });
  await deleteReview(row.id);
  ElMessage.success("删除成功");
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
        <span class="text-lg font-bold">评价列表</span>
      </template>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户" width="100" />
        <el-table-column prop="hotelName" label="酒店" min-width="150" />
        <el-table-column prop="rating" label="评分" width="80">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="200">
          <template #default="{ row }">
            <el-text line-clamp="2">{{ row.content }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="(statusMap[row.status]?.type as any) || 'info'">
              {{ statusMap[row.status]?.label || "未知" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评价时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button link type="success" @click="handleApprove(row)">
                通过
              </el-button>
              <el-button link type="warning" @click="handleReject(row)">
                拒绝
              </el-button>
            </template>
            <el-button link type="danger" @click="handleDelete(row)">
              删除
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
