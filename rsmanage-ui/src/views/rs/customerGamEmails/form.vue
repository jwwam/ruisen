<template>
	<el-dialog :title="form.emailId ? '编辑' : '新增'" v-model="visible" :close-on-click-modal="false" draggable @open="onDialogOpen">
		<el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="100px" v-loading="loading">
			<el-row :gutter="24">
				<el-col :span="12" class="mb20">
					<el-form-item label="客户姓名" prop="customerId" required>
						<el-select v-model="form.customerId" placeholder="请选择客户姓名" filterable>
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
	customerId: [{ required: true, message: '请选择客户姓名', trigger: 'change' }],
	email: [{ validator: rule.email, trigger: 'blur' }],
});

// 添加对话框打开事件处理函数
const onDialogOpen = () => {
	const isEditMode = !!form.emailId;
};

// 修改 getCustomerGamEmailsData 函数
const getCustomerGamEmailsData = async (id: string) => {
	loading.value = true;
	try {
		// 先获取客户列表和合作伙伴列表
		await Promise.all([fetchCustomers(), fetchPartners()]);

		const res = await getObj({ emailId: id });
		if (!res || !res.data) {
			return;
		}

		const data = Array.isArray(res.data) ? res.data[0] : res.data;

		// 设置表单数据
		form.emailId = data.emailId;
		form.customerId = data.customerId;
		form.email = data.email;
		form.networkCode = data.networkCode;
		form.partnerCode = data.partnerCode;
	} catch (error) {
		useMessage().error('获取数据失败');
	} finally {
		loading.value = false;
	}
};

// 修改 openDialog 函数
const openDialog = async (id?: string) => {
	// 重置表单数据
	form.emailId = '';
	form.customerId = '';
	form.email = '';
	form.networkCode = '';
	form.partnerCode = '';

	visible.value = true;
	await nextTick();

	if (id) {
		await getCustomerGamEmailsData(id);
	} else {
		if (dataFormRef.value) {
			dataFormRef.value.resetFields();
		}
	}
};

// 监听 visible 变化
watch(visible, (newVal) => {
	if (!newVal) {
		// 当对话框关闭时，重置表单
		form.emailId = '';
		form.customerId = '';
		form.email = '';
		form.networkCode = '';
		form.partnerCode = '';
		// 重置表单验证
		if (dataFormRef.value) {
			dataFormRef.value.resetFields();
		}
	}
});

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
