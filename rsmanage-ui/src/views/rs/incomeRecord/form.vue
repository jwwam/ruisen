<template>
    <el-dialog :title="form.id ? '编辑' : '新增'" v-model="visible"
      :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
       <el-row :gutter="24">
    <el-col :span="12" class="mb20">
      <el-form-item label="数据日期" prop="date">
      <el-date-picker type="date" placeholder="请选择数据日期" v-model="form.date" :value-format="dateStr"></el-date-picker>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="站点域名" prop="site">
        <el-input v-model="form.site" placeholder="请输入站点域名"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="国家/地区" prop="country">
        <el-input v-model="form.country" placeholder="请输入国家/地区"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="广告展示次数" prop="impressions">
        <el-input v-model="form.impressions" placeholder="请输入广告展示次数"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="广告点击次数" prop="clicks">
        <el-input v-model="form.clicks" placeholder="请输入广告点击次数"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="点击率(Click Through Rate)" prop="ctr">
        <el-input v-model="form.ctr" placeholder="请输入点击率(Click Through Rate)"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="广告收入(美元)" prop="revenue">
        <el-input v-model="form.revenue" placeholder="请输入广告收入(美元)"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="千次展示收益(Effective Cost Per Mille)" prop="ecpm">
        <el-input v-model="form.ecpm" placeholder="请输入千次展示收益(Effective Cost Per Mille)"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="广告请求数" prop="adRequests">
        <el-input v-model="form.adRequests" placeholder="请输入广告请求数"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="广告匹配率(%)" prop="matchRate">
        <el-input v-model="form.matchRate" placeholder="请输入广告匹配率(%)"/>
      </el-form-item>
      </el-col>

			</el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="visible = false">取 消</el-button>
          <el-button type="primary" @click="onSubmit" :disabled="loading">确 认</el-button>
        </span>
      </template>
    </el-dialog>
</template>

<script setup lang="ts" name="IncomeRecordDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/rs/incomeRecord'
import { rule } from '/@/utils/validate';
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false)
const loading = ref(false)
// 定义字典

// 提交表单数据
const form = reactive({
		id:'',
	  date: '',
	  site: '',
	  country: '',
	  impressions: '',
	  clicks: '',
	  ctr: '',
	  revenue: '',
	  ecpm: '',
	  adRequests: '',
	  matchRate: '',
});

// 定义校验规则
const dataRules = ref({
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true
  form.id = ''

  // 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

  // 获取incomeRecord信息
  if (id) {
    form.id = id
    getIncomeRecordData(id)
  }
};

// 提交
const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
    loading.value = true;
		form.id ? await putObj(form) : await addObj(form);
		useMessage().success(form.id ? '修改成功' : '添加成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg);
	} finally {
    loading.value = false;
  }
};


// 初始化表单数据
const getIncomeRecordData = (id: string) => {
  // 获取数据
  loading.value = true
  getObj({id: id}).then((res: any) => {
    Object.assign(form, res.data[0])
  }).finally(() => {
    loading.value = false
  })
};

// 暴露变量
defineExpose({
  openDialog
});
</script>
