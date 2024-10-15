<template>
    <el-dialog :title="form.partnerId ? '编辑' : '新增'" v-model="visible"
      :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
       <el-row :gutter="24">
    <el-col :span="12" class="mb20">
      <el-form-item label="合作伙伴名称" prop="partnerName">
        <el-input v-model="form.partnerName" placeholder="请输入合作伙伴名称"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="合作伙伴标识" prop="partnerCode">
        <el-input v-model="form.partnerCode" placeholder="请输入合作伙伴标识"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="分成比例" prop="revenueShare">
        <el-input-number :min="1" :max="1000" v-model="form.revenueShare" placeholder="请输入分成比例"></el-input-number>
      </el-form-item>
    </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="合作开始时间" prop="startDate">
      <el-date-picker type="date" placeholder="请选择合作开始时间" v-model="form.startDate" :value-format="dateStr"></el-date-picker>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="合作结束时间" prop="endDate">
      <el-date-picker type="date" placeholder="请选择合作结束时间" v-model="form.endDate" :value-format="dateStr"></el-date-picker>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="分成有效天数" prop="validDays">
        <el-input-number :min="1" :max="1000" v-model="form.validDays" placeholder="请输入分成有效天数"></el-input-number>
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

<script setup lang="ts" name="PartnersDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/rs/partners'
import { rule } from '/@/utils/validate';
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false)
const loading = ref(false)
// 定义字典

// 提交表单数据
const form = reactive({
		partnerId:'',
	  partnerName: '',
	  partnerCode: '',
		revenueShare: 0,
	  startDate: '',
	  endDate: '',
		validDays: 0,
});

// 定义校验规则
const dataRules = ref({
    partnerName: [{required: true, message: '合作伙伴名称不能为空', trigger: 'blur'}],
    revenueShare: [{ validator: rule.number, trigger: 'blur' }],
    validDays: [{ validator: rule.number, trigger: 'blur' }],
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true
  form.partnerId = ''

  // 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

  // 获取partners信息
  if (id) {
    form.partnerId = id
    getPartnersData(id)
  }
};

// 提交
const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
    loading.value = true;
		form.partnerId ? await putObj(form) : await addObj(form);
		useMessage().success(form.partnerId ? '修改成功' : '添加成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg);
	} finally {
    loading.value = false;
  }
};


// 初始化表单数据
const getPartnersData = (id: string) => {
  // 获取数据
  loading.value = true
  getObj({partnerId: id}).then((res: any) => {
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
