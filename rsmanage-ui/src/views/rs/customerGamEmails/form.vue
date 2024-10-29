<template>
    <el-dialog :title="form.emailId ? '编辑' : '新增'" v-model="visible"
      :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
       <el-row :gutter="24">
    <el-col :span="12" class="mb20">
      <el-form-item label="客户ID" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户ID">
            <el-option label="请选择">0</el-option>
          </el-select>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="GAM邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入GAM邮箱"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="NetworkCode" prop="networkCode">
        <el-input v-model="form.networkCode" placeholder="请输入NetworkCode"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="合作伙伴标识" prop="partnerCode">
          <el-select v-model="form.partnerCode" placeholder="请选择合作伙伴标识">
            <el-option label="请选择">0</el-option>
          </el-select>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
  <el-form-item label="ads文件内容" prop="adsTxtContent">
    <upload-file v-model="form.adsTxtContent"></upload-file>
  </el-form-item>
  </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="用户名" prop="name">
        <el-input v-model="form.name" placeholder="请输入用户名"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" placeholder="请输入密码"/>
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

<script setup lang="ts" name="CustomerGamEmailsDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/rs/customerGamEmails'
import { rule } from '/@/utils/validate';
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false)
const loading = ref(false)
// 定义字典

// 提交表单数据
const form = reactive({
		emailId:'',
	  customerId: '',
	  email: '',
	  networkCode: '',
	  partnerCode: '',
	  adsTxtContent: '',
	  name: '',
	  password: '',
});

// 定义校验规则
const dataRules = ref({
    email: [{ validator: rule.email, trigger: 'blur' }],
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true
  form.emailId = ''

  // 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

  // 获取customerGamEmails信息
  if (id) {
    form.emailId = id
    getCustomerGamEmailsData(id)
  }
};

// 提交
const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
    loading.value = true;
		form.emailId ? await putObj(form) : await addObj(form);
		useMessage().success(form.emailId ? '修改成功' : '添加成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg);
	} finally {
    loading.value = false;
  }
};


// 初始化表单数据
const getCustomerGamEmailsData = (id: string) => {
  // 获取数据
  loading.value = true
  getObj({emailId: id}).then((res: any) => {
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
