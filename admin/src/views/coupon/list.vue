<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import {
  getCouponList,
  createCoupon,
  updateCoupon,
  deleteCoupon
} from "@/api/coupon";
import { ElMessage, ElMessageBox } from "element-plus";

defineOptions({ name: "CouponList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: ""
});

const dialogVisible = ref(false);
const dialogTitle = ref("新增优惠券");
const formRef = ref();
const form = reactive({
  id: null as number | null,
  name: "",
  type: 1,
  discount: 0,
  minAmount: 0,
  startTime: "",
  endTime: "",
  totalCount: 0,
  description: ""
});

const rules = {
  name: [{ required: true, message: "请输入优惠券名称", trigger: "blur" }],
  discount: [{ required: true, message: "请输入优惠金额/折扣", trigger: "blur" }]
};

const typeOptions = [
  { label: "满减券", value: 1 },
  { label: "折扣券", value: 2 },
  { label: "代金券", value: 3 }
];

async function fetchData() {
  loading.value = true;
  try {
    const res = await getCouponList(queryParams);
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
  queryParams.keyword = "";
  handleSearch();
}

function handleAdd() {
  dialogTitle.value = "新增优惠券";
  resetForm();
  dialogVisible.value = true;
}

function handleEdit(row: any) {
  dialogTitle.value = "编辑优惠券";
  Object.assign(form, {
    id: row.id,
    name: row.name,
    type: row.type,
    discount: row.discount,
    minAmount: row.minAmount,
    startTime: row.startTime,
    endTime: row.endTime,
    totalCount: row.totalCount,
    description: row.description
  });
  dialogVisible.value = true;
}

function resetForm() {
  form.id = null;
  form.name = "";
  form.type = 1;
  form.discount = 0;
  form.minAmount = 0;
  form.startTime = "";
  form.endTime = "";
  form.totalCount = 0;
  form.description = "";
}

async function handleSubmit() {
  await formRef.value?.validate();
  const data = { ...form };
  if (form.id) {
    await updateCoupon(form.id, data);
    ElMessage.success("更新成功");
  } else {
    await createCoupon(data);
    ElMessage.success("创建成功");
  }
  dialogVisible.value = false;
  fetchData();
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm("确认删除该优惠券？", "提示", {
    type: "warning"
  });
  await deleteCoupon(row.id);
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
        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="优惠券名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="text-lg font-bold">优惠券列表</span>
          <el-button type="primary" @click="handleAdd">新增优惠券</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            {{ typeOptions.find(t => t.value === row.type)?.label || "未知" }}
          </template>
        </el-table-column>
        <el-table-column prop="discount" label="优惠值" width="100" />
        <el-table-column prop="minAmount" label="最低消费" width="100">
          <template #default="{ row }">¥{{ row.minAmount }}</template>
        </el-table-column>
        <el-table-column prop="totalCount" label="总数量" width="80" />
        <el-table-column prop="usedCount" label="已使用" width="80" />
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column prop="endTime" label="结束时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type">
            <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="优惠值" prop="discount">
          <el-input-number v-model="form.discount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="最低消费">
          <el-input-number v-model="form.minAmount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="发放数量">
          <el-input-number v-model="form.totalCount" :min="0" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
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
.justify-between {
  justify-content: space-between;
}
.justify-end {
  justify-content: flex-end;
}
.items-center {
  align-items: center;
}
.text-lg {
  font-size: 18px;
}
.font-bold {
  font-weight: bold;
}
</style>
