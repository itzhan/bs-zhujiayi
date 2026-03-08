<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import {
  getHotelList,
  createHotel,
  updateHotel,
  deleteHotel,
  updateHotelStatus
} from "@/api/hotel";
import { ElMessage, ElMessageBox } from "element-plus";

defineOptions({ name: "HotelList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: "",
  status: ""
});

const dialogVisible = ref(false);
const dialogTitle = ref("新增酒店");
const formRef = ref();
const form = reactive({
  id: null as number | null,
  name: "",
  address: "",
  phone: "",
  description: "",
  starRating: 3,
  status: 1
});

const rules = {
  name: [{ required: true, message: "请输入酒店名称", trigger: "blur" }],
  address: [{ required: true, message: "请输入酒店地址", trigger: "blur" }],
  phone: [{ required: true, message: "请输入联系电话", trigger: "blur" }]
};

const statusOptions = [
  { label: "全部", value: "" },
  { label: "营业中", value: 1 },
  { label: "已关闭", value: 0 }
];

async function fetchData() {
  loading.value = true;
  try {
    const res = await getHotelList(queryParams);
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
  queryParams.status = "";
  handleSearch();
}

function handleAdd() {
  dialogTitle.value = "新增酒店";
  resetForm();
  dialogVisible.value = true;
}

function handleEdit(row: any) {
  dialogTitle.value = "编辑酒店";
  Object.assign(form, {
    id: row.id,
    name: row.name,
    address: row.address,
    phone: row.phone,
    description: row.description,
    starRating: row.starRating,
    status: row.status
  });
  dialogVisible.value = true;
}

function resetForm() {
  form.id = null;
  form.name = "";
  form.address = "";
  form.phone = "";
  form.description = "";
  form.starRating = 3;
  form.status = 1;
}

async function handleSubmit() {
  await formRef.value?.validate();
  const data = { ...form };
  if (form.id) {
    await updateHotel(form.id, data);
    ElMessage.success("更新成功");
  } else {
    await createHotel(data);
    ElMessage.success("创建成功");
  }
  dialogVisible.value = false;
  fetchData();
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm("确认删除该酒店？", "提示", {
    type: "warning"
  });
  await deleteHotel(row.id);
  ElMessage.success("删除成功");
  fetchData();
}

async function handleStatusChange(row: any) {
  const newStatus = row.status === 1 ? 0 : 1;
  await updateHotelStatus(row.id, newStatus);
  ElMessage.success("状态更新成功");
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
            placeholder="酒店名称/地址"
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
        <div class="flex justify-between items-center">
          <span class="text-lg font-bold">酒店列表</span>
          <el-button type="primary" @click="handleAdd">新增酒店</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="酒店名称" min-width="150" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="starRating" label="星级" width="80">
          <template #default="{ row }">{{ row.starRating }}星</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "营业中" : "已关闭" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button link type="primary" @click="handleStatusChange(row)">
              {{ row.status === 1 ? "关闭" : "开启" }}
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
        <el-form-item label="酒店名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入酒店名称" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="星级">
          <el-rate v-model="form.starRating" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入酒店描述"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">营业中</el-radio>
            <el-radio :value="0">已关闭</el-radio>
          </el-radio-group>
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
