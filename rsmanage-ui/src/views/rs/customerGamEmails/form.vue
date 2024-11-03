<template>
	<el-dialog :title="form.emailId ? '编辑' : '新增'" v-model="visible" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="100px" v-loading="loading">
			<el-row :gutter="24">
				<el-col :span="12" class="mb20">
					<el-form-item label="客户姓名" prop="customerId">
						<el-select v-model="form.customerId" placeholder="" filterable>
							<el-option v-for="customer in customers" :key="customer.customerId" :label="customer.name" :value="customer.customerId" />
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="GAM邮箱" prop="email">
						<el-input v-model="form.email" placeholder="请输入GAM邮箱" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="NetworkCode" prop="networkCode">
						<el-input v-model="form.networkCode" placeholder="请输入NetworkCode" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="合作伙伴标识" prop="partnerCode">
						<el-select v-model="form.partnerCode" placeholder="请选择合作伙伴标识" filterable>
							<el-option v-for="partner in partners" :key="partner.partnerId" :label="partner.partnerCode" :value="partner.partnerCode" />
						</el-select>
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
import { useMessage } from '/@/hooks/message';
import { getObj, addObj, putObj, validateExist } from '/@/api/rs/customerGamEmails';
import { fetchList as fetchCustomerList } from '/@/api/rs/customers';
import { fetchList as partnerList } from '/@/api/rs/partners';
import { rule } from '/@/utils/validate';
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
// 定义客户对象的类型
interface Customer {
	customerId: string;
	name: string;
}
interface Partners {
	partnerId: string;
	partnerCode: string;
}
// 提交表单数据
const form = reactive({
	emailId: '',
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
});

// 打开弹窗
const openDialog = (id: string) => {
	visible.value = true;
	form.emailId = '';

	// 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

	// 获取customerGamEmails信息
	if (id) {
		form.emailId = id;
		getCustomerGamEmailsData(id);
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
	loading.value = true;
	getObj({ emailId: id })
		.then((res: any) => {
			Object.assign(form, res.data[0]);
		})
		.finally(() => {
			loading.value = false;
		});
};
const customers = ref<Customer[]>([]); // 使用定义的类型
const partners = ref<Partners[]>([]);
const fetchCustomers = async () => {
	try {
		const response = await fetchCustomerList();
		customers.value = response.data.records as Customer[]; // 假设返回的数据结构中客户列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch customers:', error);
	}
};
const fetchPartners = async () => {
	try {
		const response = await partnerList();
		partners.value = response.data.records as Partners[]; // 假设返回的数据结构中客户列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch partners:', error);
	}
};
onMounted(() => {
	fetchCustomers();
	fetchPartners();
});
// 暴露变量
defineExpose({
	openDialog,
});
</script>
