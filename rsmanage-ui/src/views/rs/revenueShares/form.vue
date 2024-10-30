<template>
    <el-dialog :title="form.shareId ? '编辑' : '新增'" v-model="visible"
      :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
       <el-row :gutter="24">
    <el-col :span="12" class="mb20">
      <el-form-item label="合作伙伴ID" prop="partnerId">
          <el-select v-model="form.partnerId" placeholder="请选择合作伙伴ID">
            <el-option label="请选择">0</el-option>
          </el-select>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="分成比例名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入分成比例名称"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="分成比例" prop="share">
        <el-input v-model="form.share" placeholder="请输入分成比例"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="分成比例描述" prop="description">
        <el-input v-model="form.description" placeholder="请输入分成比例描述"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="有效天数" prop="validDays">
        <el-input v-model="form.validDays" placeholder="请输入有效天数"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="是否启用" prop="isActive">
            <el-radio-group v-model="form.isActive">
             <el-radio :label="item.value" v-for="(item, index) in yes_no_type" border :key="index">{{ item.label }}
            </el-radio>
            </el-radio-group>
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

<script setup lang="ts" name="RevenueSharesDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/rs/revenueShares'
import { rule } from '/@/utils/validate';
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false)
const loading = ref(false)
// 定义字典
const { yes_no_type } = useDict('yes_no_type')

// 提交表单数据
const form = reactive({
		shareId:'',
	  partnerId: '',
	  name: '',
	  share: '',
	  description: '',
	  validDays: '',
	  isActive: '',
});

// 定义校验规则
const dataRules = ref({
    partnerId: [{required: true, message: '合作伙伴ID不能为空', trigger: 'blur'}],
    validDays: [{ validator: rule.number, trigger: 'blur' }],
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true;
  form.shareId = '';

  // 重置表单数据
  nextTick(() => {
    dataFormRef.value?.resetFields();
  });

  // 获取revenueShares信息
  if (id) {
    form.shareId = id;
    getRevenueSharesData(id);
  }
};

// 提交
const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
    loading.value = true;
		form.shareId ? await putObj(form) : await addObj(form);
		useMessage().success(form.shareId ? '修改成功' : '添加成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg);
	} finally {
    loading.value = false;
  }
};


// 初始化表单数据
const getRevenueSharesData = (id: string) => {
  // 获取数据
  loading.value = true
  getObj({shareId: id}).then((res: any) => {
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
