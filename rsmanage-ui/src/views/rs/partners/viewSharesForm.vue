<template>
  <el-dialog :title="'查看分成比例'" v-model="visible" :close-on-click-modal="false" draggable>
    <el-table :data="revenueShares" border>
      <el-table-column prop="name" label="名称" align="center" />
      <el-table-column prop="share" label="分成比例" align="center">
        <template #default="scope">
          {{ scope.row.share }}%
        </template>
      </el-table-column>
      <el-table-column prop="validDays" label="有效天数" align="center" />
      <el-table-column prop="isActive" label="是否启用" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.isActive ? 'success' : 'info'">
            {{ scope.row.isActive ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" align="center" />
    </el-table>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { fetchList } from "/@/api/rs/revenueShares";

const visible = ref(false);
const revenueShares = ref();

const openDialog = (partnerId: string) => {
  fetchList({partnerId: partnerId}).then((res) => {
    // console.log(res);
    revenueShares.value = res.data.records;
    visible.value = true;
  });
};

defineExpose({
  openDialog
});
</script>
