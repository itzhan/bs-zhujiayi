<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import {
  getRoomTypeList,
  createRoomType,
  updateRoomType,
  deleteRoomType
} from "@/api/roomType";
import { getHotelList } from "@/api/hotel";
import { ElMessage, ElMessageBox } from "element-plus";

defineOptions({ name: "RoomTypeList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const hotelOptions = ref([]);
const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: "",
  hotelId: ""
});

const dialogVisible = ref(false);
const dialogTitle = ref("新增房型");
const formRef = ref();
const form = reactive({
  id: null as number | null,
  hotelId: null as number | null,
  name: "",
  price: 0,
  capacity: 2,
  bedType: "",
  area: 0,
  description: "",
  totalRooms: 0,
  availableRooms: 0
});

const rules = {
  hotelId: [{ required: true, message: "请选择所属酒店", trigger: "change" }],
  name: [{ required: true, message: "请输入房型名称", trigger: "blur" }],
  price: [{ required: true, message: "请输入房间价格", trigger: "blur" }]
};

async function fetchData() {
  loading.value = true;
  try {
    const res = await getRoomTypeList(queryParams);
    if (res.code === 200) {
      tableData.value = res.data.records;
      total.value = res.data.total;
    }
  } finally {
    loading.value = false;
  }
}

async function fetchHotels() {
  const res = await getHotelList({ page: 1, size: 1000 });
  if (res.code === 200) {
    hotelOptions.value = res.data.records;
  }
}

function handleSearch() {
  queryParams.page = 1;
  fetchData();
}

function resetQuery() {
  queryParams.keyword = "";
  queryParams.hotelId = "";
  handleSearch();
}

function handleAdd() {
  dialogTitle.value = "新增房型";
  resetForm();
  dialogVisible.value = true;
}

function handleEdit(row: any) {
  dialogTitle.value = "编辑房型";
  Object.assign(form, {
    id: row.id,
    hotelId: row.hotelId,
    name: row.name,
    price: row.price,
    capacity: row.capacity,
    bedType: row.bedType,
    area: row.area,
    description: row.description,
    totalRooms: row.totalRooms,
    availableRooms: row.availableRooms
  });
  dialogVisible.value = true;
}

function resetForm() {
  form.id = null;
  form.hotelId = null;
  form.name = "";
  form.price = 0;
  form.capacity = 2;
  form.bedType = "";
  form.area = 0;
  form.description = "";
  form.totalRooms = 0;
  form.availableRooms = 0;
}

async function handleSubmit() {
  await formRef.value?.validate();
  const data = { ...form };
  if (form.id) {
    await updateRoomType(form.id, data);
    ElMessage.success("更新成功");
  } else {
    await createRoomType(data);
    ElMessage.success("创建成功");
  }
  dialogVisible.value = false;
  fetchData();
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm("确认删除该房型？", "提示", {
    type: "warning"
  });
  await deleteRoomType(row.id);
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

onMounted(() => {
  fetchData();
  fetchHotels();
});
</script>

<template>
  <div class="main">
    <el-card shadow="never" class="mb-4">
      <el-form :model="queryParams" inline>
        <el-form-item label="所属酒店">
          <el-select
            v-model="queryParams.hotelId"
            clearable
            placeholder="全部酒店"
          >
            <el-option
              v-for="item in hotelOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="房型名称"
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
          <span class="text-lg font-bold">房型列表</span>
          <el-button type="primary" @click="handleAdd">新增房型</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="hotelName" label="所属酒店" min-width="150" />
        <el-table-column prop="name" label="房型名称" min-width="120" />
        <el-table-column prop="price" label="价格(元/晚)" width="120">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="bedType" label="床型" width="100" />
        <el-table-column prop="capacity" label="容纳人数" width="90" />
        <el-table-column prop="area" label="面积(㎡)" width="90" />
        <el-table-column prop="totalRooms" label="总房间数" width="90" />
        <el-table-column prop="availableRooms" label="可用数" width="80" />
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
        <el-form-item label="所属酒店" prop="hotelId">
          <el-select v-model="form.hotelId" placeholder="请选择酒店">
            <el-option
              v-for="item in hotelOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入房型名称" />
        </el-form-item>
        <el-form-item label="价格(元)" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="床型">
          <el-input v-model="form.bedType" placeholder="如：大床/双床" />
        </el-form-item>
        <el-form-item label="容纳人数">
          <el-input-number v-model="form.capacity" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="面积(㎡)">
          <el-input-number v-model="form.area" :min="0" />
        </el-form-item>
        <el-form-item label="总房间数">
          <el-input-number v-model="form.totalRooms" :min="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入房型描述"
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
