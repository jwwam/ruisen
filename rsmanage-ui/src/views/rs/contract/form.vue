<template>
    <el-dialog :title="form.contractId ? '编辑' : '新增'" v-model="visible"
      :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
       <el-row :gutter="24">
    <el-col :span="12" class="mb20">
      <el-form-item label="合同编号" prop="contractNumber">
        <el-input v-model="form.contractNumber" placeholder="请输入合同编号"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="客户ID" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户ID">
            <el-option label="请选择">0</el-option>
          </el-select>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="合同是否生效" prop="isActive">
            <el-radio-group v-model="form.isActive">
             <el-radio :label="item.value" v-for="(item, index) in yes_no_type" border :key="index">{{ item.label }}
            </el-radio>
            </el-radio-group>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="签署日期" prop="signedDate">
      <el-date-picker type="date" placeholder="请选择签署日期" v-model="form.signedDate" :value-format="dateStr"></el-date-picker>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="收款人名称" prop="payeeName">
        <el-input v-model="form.payeeName" placeholder="请输入收款人名称"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="收款人账号" prop="payeeAccount">
        <el-input v-model="form.payeeAccount" placeholder="请输入收款人账号"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="银行名称" prop="bankName">
        <el-input v-model="form.bankName" placeholder="请输入银行名称"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="银行地址" prop="bankAddress">
        <el-input v-model="form.bankAddress" placeholder="请输入银行地址"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="SwiftCode" prop="swiftCode">
        <el-input v-model="form.swiftCode" placeholder="请输入SwiftCode"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
  <el-form-item label="合同文件" prop="fileUrl">
    <upload-file v-model="form.fileUrl"></upload-file>
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

<script setup lang="ts" name="ContractDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/rs/contract'
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
		contractId:'',
	  contractNumber: '',
	  customerId: '',
	  isActive: '',
	  signedDate: '',
	  payeeName: '',
	  payeeAccount: '',
	  bankName: '',
	  bankAddress: '',
	  swiftCode: '',
	  fileUrl: '',
});

// 定义校验规则
const dataRules = ref({
    customerId: [{required: true, message: '客户ID不能为空', trigger: 'blur'}],
    isActive: [{required: true, message: '合同是否生效不能为空', trigger: 'blur'}],
    signedDate: [{required: true, message: '签署日期不能为空', trigger: 'blur'}],
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true
  form.contractId = ''

  // 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

  // 获取contract信息
  if (id) {
    form.contractId = id
    getContractData(id)
  }
};

// 提交
const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
    loading.value = true;
		form.contractId ? await putObj(form) : await addObj(form);
		useMessage().success(form.contractId ? '修改成功' : '添加成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg);
	} finally {
    loading.value = false;
  }
};


// 初始化表单数据
const getContractData = (id: string) => {
  // 获取数据
  loading.value = true
  getObj({contractId: id}).then((res: any) => {
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
