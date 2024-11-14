<template>
	<el-dialog :title="isAdd ? '新增合同' : readonly ? '查看合同' : '编辑合同'" v-model="visible" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="110px" v-loading="loading">
			<el-row :gutter="24">
				<el-col :span="12" class="mb20">
					<el-form-item label="合同编号" prop="contractNumber">
						<el-input v-model="form.contractNumber" placeholder="自动生成" disabled />
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="合同名称" prop="contractName">
						<el-input v-model="form.contractName" placeholder="请输入合同名称" :disabled="readonly" />
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="签署日期" prop="signedDate">
						<el-date-picker
							type="date"
							placeholder="请选择签署日期"
							v-model="form.signedDate"
							:value-format="dateStr"
							:disabled="readonly"
						></el-date-picker>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="客户姓名" prop="customerId">
						<el-select v-model="form.customerId" placeholder="请选择客户" filterable :disabled="readonly">
							<el-option v-for="customer in customers" :key="customer.customerId" :label="customer.name" :value="customer.customerId" />
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="合同是否生效" prop="isActive">
						<el-radio-group v-model="form.isActive" :disabled="readonly">
							<el-radio :value="item.value" v-for="(item, index) in yes_no_type" border :key="index">{{ item.label }}</el-radio>
						</el-radio-group>
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="SwiftCode" prop="swiftCode">
						<el-input v-model="form.swiftCode" placeholder="请输入SwiftCode" :disabled="readonly" />
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="收款人名称" prop="payeeName">
						<el-input v-model="form.payeeName" placeholder="请输入收款人名称" :disabled="readonly" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="收款人账号" prop="payeeAccount">
						<el-input v-model="form.payeeAccount" placeholder="请输入收款人账号" :disabled="readonly" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="银行名称" prop="bankName">
						<el-input v-model="form.bankName" placeholder="请输入银行名称" :disabled="readonly" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="银行地址" prop="bankAddress">
						<el-input v-model="form.bankAddress" placeholder="请输入银行地址" :disabled="readonly" />
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="24" class="mb20">
					<el-form-item label="合同文件" prop="fileUrl">
						<upload-file v-model="form.fileUrl" :disabled="readonly"></upload-file>
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
import { getObj as getContract, addObj, putObj, validateExist, getNextSequence } from '/@/api/rs/contract';
import { fetchListWithoutRole as fetchCustomerList } from '/@/api/rs/customers'; // 引入客户表信息
import { rule } from '/@/utils/validate';
import { useUserInfo } from '/@/stores/userInfo'; // 引入用户信息
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
const readonly = ref(false);

// 新增状态标识
const isAdd = ref(false);

// 定义字典
const { yes_no_type } = useDict('yes_no_type');
// 定义客户对象的类型
interface Customer {
	customerId: string;
	name: string;
}
// 定义表单默认值
const defaultForm = {
	contractId: '',
	contractNumber: '',
	contractName: '',
	customerId: '',
	isActive: '',
	signedDate: '',
	payeeName: '',
	payeeAccount: '',
	bankName: '',
	bankAddress: '',
	swiftCode: '',
	fileUrl: '',
	createUserId: '',
};

// 表单对象
const form = reactive({ ...defaultForm });

// 定义校验规则
const dataRules = ref({
	customerId: [{ required: true, message: '客户ID不能为空', trigger: 'blur' }],
	isActive: [{ required: true, message: '合同是否生效不能为空', trigger: 'blur' }],
	signedDate: [{ required: true, message: '签署日期不能为空', trigger: 'blur' }],
	contractName: [{ required: true, message: '请输入合同名称', trigger: 'blur' }],
});

// 在 script setup 顶部添加
const userStore = useUserInfo();

// 打开弹窗
const openDialog = async (id?: string, isReadonly: boolean = false) => {
	console.log('openDialog 被调用:', { id, isReadonly });

	visible.value = true;
	isAdd.value = !id;
	readonly.value = isReadonly;

	if (id) {
		loading.value = true;
		try {
			const res = await getContract(id);

			if (res.data && Array.isArray(res.data)) {
				// 在数组中查找匹配的记录
				const contractData = res.data.find((item) => item.contractId.toString() === id.toString());

				if (contractData) {
					// 转换 isActive 为字符串类型
					const formData = {
						...contractData,
						isActive: contractData.isActive.toString(), // 将数字转换为字符串
					};

					// 设置到表单
					Object.assign(form, formData);
				} else {
					console.warn('未找到匹配的合同数据');
					useMessage().warning('未找到合同数据');
				}
			} else {
				console.warn('API返回的数据格式不正确');
			}
		} catch (error) {
			console.error('获取数据失败:', error);
			useMessage().error('获取数据失败');
		} finally {
			loading.value = false;
		}
	} else {
		// 新增模式
		Object.assign(form, defaultForm);
		form.createUserId = currentUserId.value;
	}

	// 延迟重置验证状态
	nextTick(() => {
		dataFormRef.value?.clearValidate();
	});
};

// 提交
const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
		loading.value = true;
		if (isAdd.value) {
			// 新增逻辑
			form.createUserId = currentUserId.value;
			await addObj(form);
			useMessage().success('添加成功');
		} else {
			// 编辑逻辑
			await putObj(form);
			useMessage().success('修改成功');
		}
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
	getContract({ contractId: id })
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

// 获取当前用户信息
const currentUserId = ref('');

const fetchCurrentUser = () => {
	const data = useUserInfo().userInfos;

	currentUserId.value = data.user.userId;
};

// 在 onMounted 中调用
onMounted(() => {
	fetchCurrentUser();
	fetchCustomers();
});

// 可以添加一个计算属性判断是否有编辑权限
const hasEditPermission = computed(() => {
	return currentUserId.value === form.createUserId;
});

// 监听签署日期变化
watch(
	() => form.signedDate,
	async (newDate) => {
		// 只在新增模式下生成合同编号
		if (newDate && isAdd.value) {
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
