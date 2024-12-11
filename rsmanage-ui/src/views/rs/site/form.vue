<template>
    <el-dialog :title="form.id ? '编辑' : '新增'" v-model="visible"
      :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
       <el-row :gutter="24">
    <el-col :span="12" class="mb20">
      <el-form-item label="域名" prop="domain">
        <el-input v-model="form.domain" placeholder="请输入域名"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="状态 1-正常 2-禁用" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态 1-正常 2-禁用">
            <el-option :value="item.value" :label="item.label" v-for="(item, index) in site_status" :key="index"></el-option>
          </el-select>
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
      <el-form-item label="上游合作伙伴ID列表，多个用逗号分隔" prop="partnerIds">
            <el-checkbox-group v-model="form.partnerIds">
                <el-checkbox label="启用" name="type"></el-checkbox>
                <el-checkbox label="禁用" name="type"></el-checkbox>
            </el-checkbox-group>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="是否借用账号 0-否 1-是" prop="isBorrowedAccount">
          <el-select v-model="form.isBorrowedAccount" placeholder="请选择是否借用账号 0-否 1-是">
            <el-option :value="item.value" :label="item.label" v-for="(item, index) in yes_no_type" :key="index"></el-option>
          </el-select>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="借用账号客户ID" prop="borrowedCustomerId">
          <el-select v-model="form.borrowedCustomerId" placeholder="请选择借用账号客户ID">
            <el-option label="请选择">0</el-option>
          </el-select>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="客户GAM邮箱" prop="gamEmailId">
          <el-select v-model="form.gamEmailId" placeholder="请选择客户GAM邮箱">
            <el-option label="请选择">0</el-option>
          </el-select>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="是否放链接 0-否 1-是" prop="hasLink">
            <el-radio-group v-model="form.hasLink">
             <el-radio :label="item.value" v-for="(item, index) in yes_no_type" border :key="index">{{ item.label }}
            </el-radio>
            </el-radio-group>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="站点提审账号" prop="reviewAccount">
        <el-input v-model="form.reviewAccount" placeholder="请输入站点提审账号"/>
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

<script setup lang="ts" name="SiteDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/rs/site'
import { rule } from '/@/utils/validate';
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false)
const loading = ref(false)
// 定义字典
const { site_status,yes_no_type } = useDict('site_status','yes_no_type')

// 提交表单数据
const form = reactive({
		id:'',
	  domain: '',
	  status: '',
	  customerId: '',
    partnerIds: [],
	  isBorrowedAccount: '',
	  borrowedCustomerId: '',
	  gamEmailId: '',
	  hasLink: '',
	  reviewAccount: '',
});

// 定义校验规则
const dataRules = ref({
    domain: [{required: true, message: '域名不能为空', trigger: 'blur'}],
    status: [{required: true, message: '状态 1-正常 2-禁用不能为空', trigger: 'blur'}],
    isBorrowedAccount: [{required: true, message: '是否借用账号 0-否 1-是不能为空', trigger: 'blur'}],
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true
  form.id = ''

  // 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

  // 获取site信息
  if (id) {
    form.id = id
    getSiteData(id)
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
const getSiteData = (id: string) => {
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
