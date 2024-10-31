<template>
	<el-dialog :title="form.relationId ? '编辑' : '新增'" v-model="visible" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="140px" v-loading="loading">
			<el-row :gutter="25">
				<el-col :span="12" class="mb20">
					<el-form-item label="客户ID" prop="customerId">
						<el-select v-model="form.customerId" placeholder="请选择客户ID">
							<el-option v-for="customer in customers" :key="customer.customerId" :label="customer.name" :value="customer.customerId" />
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="合作伙伴标识" prop="partnerCode">
						<el-select v-model="form.partnerCode" placeholder="请选择合作伙伴标识">
							<el-option v-for="partner in partners" :key="partner.partnerId" :label="partner.partnerCode" :value="partner.partnerCode" />
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="客户分成比例" prop="customerRevenueShare">
						<el-input v-model="form.customerRevenueShare" placeholder="请输入客户分成比例（百分比）" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="合作伙伴平台的账号" prop="partnerAccount">
						<el-input v-model="form.partnerAccount" placeholder="请输入客户在合作伙伴平台的账号" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="合作伙伴平台的密码" prop="partnerPassword">
						<el-input v-model="form.partnerPassword" placeholder="请输入客户在合作伙伴平台的密码" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="合作伙伴平台的地址" prop="url">
						<el-input v-model="form.url" placeholder="请输入客户在合作伙伴平台的地址" />
					</el-form-item>
				</el-col>

				<el-col :span="15" class="mb20">
					<el-form-item label="对应的ads.txt内容" prop="adsTxtContent">
						<upload-file v-model="form.adsTxtContent"></upload-file>
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

<script setup lang="ts" name="CustomerPartnerDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from '/@/hooks/message';
import { fetchList as fetchCustomerList } from '/@/api/rs/customers';
import { fetchList as partnerList } from '/@/api/rs/partners';
import { getObj, addObj, putObj, validateExist } from '/@/api/rs/customerPartner';
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
	relationId: '',
	customerId: '',
	customerName: '',
	partnerCode: '',
	customerRevenueShare: '',
	partnerAccount: '',
	partnerPassword: '',
	adsTxtContent: '',
	url: '',
});

// 定义校验规则
const dataRules = ref({
	customerId: [{ required: true, message: '客户ID不能为空', trigger: 'blur' }],
	partnerCode: [{ required: true, message: '合作伙伴标识不能为空', trigger: 'blur' }],
	customerRevenueShare: [{ required: true, message: '客户分成比例（百分比）不能为空', trigger: 'blur' }],
});

// 打开弹窗
const openDialog = (id: string) => {
	visible.value = true;
	form.relationId = '';

	// 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

	// 获取customerPartner信息
	if (id) {
		form.relationId = id;
		getCustomerPartnerData(id);
	}
};

// 提交
const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
		loading.value = true;
		form.relationId ? await putObj(form) : await addObj(form);
		useMessage().success(form.relationId ? '修改成功' : '添加成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg);
	} finally {
		loading.value = false;
	}
};
// 定义变量内容
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

// 初始化表单数据
const getCustomerPartnerData = (id: string) => {
	// 获取数据
	loading.value = true;
	getObj({ relationId: id })
		.then((res: any) => {
			Object.assign(form, res.data[0]);
		})
		.finally(() => {
			loading.value = false;
		});
};

// 暴露变量
defineExpose({
	openDialog,
});
</script>
