<template>
	<el-dialog :title="form.contractId ? '编辑' : '新增'" v-model="visible" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="110px" v-loading="loading">
			<el-row :gutter="24">
				<el-col :span="12" class="mb20">
					<el-form-item label="合同编号" prop="contractNumber">
						<el-input v-model="form.contractNumber" placeholder="自动生成" disabled />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="客户姓名" prop="customerId">
						<el-select v-model="form.customerId" placeholder="" filterable>
							<el-option v-for="customer in customers" :key="customer.customerId" :label="customer.name" :value="customer.customerId" />
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="合同是否生效" prop="isActive">
						<el-radio-group v-model="form.isActive">
							<el-radio :label="item.value" v-for="(item, index) in yes_no_type" border :key="index">{{ item.label }} </el-radio>
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
						<el-input v-model="form.payeeName" placeholder="请输入收款人名称" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="收款人账号" prop="payeeAccount">
						<el-input v-model="form.payeeAccount" placeholder="请输入收款人账号" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="银行名称" prop="bankName">
						<el-input v-model="form.bankName" placeholder="请输入银行名称" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="银行地址" prop="bankAddress">
						<el-input v-model="form.bankAddress" placeholder="请输入银行地址" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="SwiftCode" prop="swiftCode">
						<el-input v-model="form.swiftCode" placeholder="请输入SwiftCode" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="24" class="mb20">
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
import { useMessage } from '/@/hooks/message';
import { getObj, addObj, putObj, validateExist, getNextSequence } from '/@/api/rs/contract';
import { fetchListWithoutRole as fetchCustomerList } from '/@/api/rs/customers'; // 引入客户表信息
import { rule } from '/@/utils/validate';
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
// 定义字典
const { yes_no_type } = useDict('yes_no_type');
// 定义客户对象的类型
interface Customer {
	customerId: string;
	name: string;
}
// 提交表单数据
const form = reactive({
	contractId: '',
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
	customerId: [{ required: true, message: '客户ID不能为空', trigger: 'blur' }],
	isActive: [{ required: true, message: '合同是否生效不能为空', trigger: 'blur' }],
	signedDate: [{ required: true, message: '签署日期不能为空', trigger: 'blur' }],
});

// 打开弹窗
const openDialog = (id: string) => {
	visible.value = true;
	form.contractId = '';

	// 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

	// 获取contract信息
	if (id) {
		form.contractId = id;
		getContractData(id);
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
	loading.value = true;
	getObj({ contractId: id })
		.then((res: any) => {
			const contractData = res.data[0];
			contractData.isActive = String(contractData.isActive);
			Object.assign(form, contractData);
		})
		.finally(() => {
			loading.value = false;
		});
};
const customers = ref<Customer[]>([]); // 使用定义的类型
const fetchCustomers = async () => {
	try {
		const response = await fetchCustomerList();
		customers.value = response.data.records as Customer[]; // 假设返回的数据结构中客户列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch customers:', error);
	}
};
onMounted(() => {
	fetchCustomers();
});

// 监听签署日期变化
watch(
	() => form.signedDate,
	async (newDate) => {
		// 只在新增时（没有contractId）且有签署日期时生成合同编号
		if (newDate && !form.contractId) {
			await generateContractNumber(newDate);
		}
	}
);

// 生成合同编号的方法
const generateContractNumber = async (date: string) => {
	try {
		// 确保日期格式正确（YYYY-MM-DD）
		const formattedDate = date.substring(0, 10);

		// 使用 URLSearchParams 确保参数正确编码
		const params = new URLSearchParams();
		params.append('signedDate', formattedDate);

		const response = await getNextSequence(formattedDate);

		// 设置合同编号
		form.contractNumber = response.data;

		console.log('签署日期：', formattedDate);
		console.log('合同编号：', form.contractNumber);
	} catch (error) {
		console.error('生成合同编号失败:', error);
		useMessage().error('生成合同编号失败');
	}
};

// 暴露变量
defineExpose({
	openDialog,
});
</script>
