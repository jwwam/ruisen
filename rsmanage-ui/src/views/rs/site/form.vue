<template>
	<el-dialog :title="form.id ? '编辑' : '新增'" v-model="visible" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="form" :rules="dataRules" label-width="110px" v-loading="loading">
			<el-row :gutter="24">
				<el-col :span="12" class="mb20">
					<el-form-item label="域名" prop="domain">
						<el-input v-model="form.domain" placeholder="请输入域名" />
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="状态" prop="status">
						<el-select v-model="form.status" placeholder="请选择状态">
							<el-option label="正常" value="1" />
							<el-option label="禁用" value="2" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="客户姓名" prop="customerId">
						<el-select v-model="form.customerId" placeholder="请选择客户" filterable :loading="loading">
							<el-option v-for="customer in customers" :key="customer.customerId" :label="customer.name" :value="String(customer.customerId)" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="合作伙伴" prop="partnerIds">
						<el-select v-model="form.partnerIds" placeholder="请选择合作伙伴" filterable multiple collapse-tags collapse-tags-tooltip>
							<el-option v-for="partner in partners" :key="partner.partnerId" :label="partner.partnerCode" :value="String(partner.partnerId)" />
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="是否借用账号" prop="isBorrowedAccount">
						<el-radio-group v-model="form.isBorrowedAccount">
							<el-radio value="0" border>否</el-radio>
							<el-radio value="1" border>是</el-radio>
						</el-radio-group>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20" v-if="form.isBorrowedAccount === '1'">
					<el-form-item label="借用账号客户" prop="borrowedCustomerId">
						<el-select v-model="form.borrowedCustomerId" placeholder="请选择借用账号客户" filterable :loading="loading">
							<el-option label="请选择" value="" />
							<el-option v-for="customer in customers" :key="customer.customerId" :label="customer.name" :value="String(customer.customerId)" />
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="客户GAM邮箱" prop="gamEmailId">
						<el-select v-model="form.gamEmailId" placeholder="请选择客户邮箱" filterable :disabled="readonly">
							<el-option v-for="gamemail in gamemail" :key="gamemail.emailId" :label="gamemail.email" :value="String(gamemail.emailId)" />
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="是否放链接" prop="hasLink">
						<el-radio-group v-model="form.hasLink">
							<el-radio value="0" border>否</el-radio>
							<el-radio value="1" border>是</el-radio>
						</el-radio-group>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="站点提审账号" prop="reviewAccount">
						<el-input v-model="form.reviewAccount" placeholder="请输入站点提审账号" />
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
import { useMessage } from '/@/hooks/message';
import { getObj, addObj, putObj } from '/@/api/rs/site';
import { fetchList as fetchCustomerList } from '/@/api/rs/customers';
import { fetchList as partnerList } from '/@/api/rs/partners';
import { fetchListWithoutRole as fetchGamEmailList } from '/@/api/rs/customerGamEmails'; // 引入合作伙伴表信息
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
const readonly = ref(false);
// 定义字典
const { yes_no_type } = useDict('yes_no_type');

// 添加监听器来查看数据变化
watch(
	() => yes_no_type.value,
	(newVal) => {
		console.log('是否类型字典数据:', newVal);
	},
	{ immediate: true }
);

// 修改计算属性
const statusText = computed(() => {
	const statusMap: { [key: string]: string } = {
		'1': '正常',
		'2': '禁用',
	};
	return statusMap[form.status] || form.status;
});

interface Customer {
	customerId: string;
	name: string;
}
interface Partners {
	partnerId: string;
	partnerCode: string;
}
interface GamEmail {
	emailId: string | number;
	email: string;
}
// 提交表单数据
const form = reactive({
	id: '',
	customerId: '',
	domain: '',
	status: '1',
	isBorrowedAccount: '0',
	borrowedCustomerId: '',
	gamEmailId: '',
	hasLink: '0',
	reviewAccount: '',
	partnerIds: '' as string | string[],
});

// 定义校验规则
const dataRules = ref({
	domain: [{ required: true, message: '域名不能为空', trigger: 'blur' }],
	status: [{ required: true, message: '状态 1-正常 2-禁用不能为空', trigger: 'blur' }],
	isBorrowedAccount: [{ required: true, message: '是否借用账号 0-否 1-是 不能为空', trigger: 'blur' }],
	customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
	partnerIds: [{ required: true, message: '请选择上游合作伙伴', trigger: 'change' }],
});

// 打开弹窗
const openDialog = async (row?: any) => {
	// 重置表单数据
	form.id = '';
	form.customerId = '';
	form.domain = '';
	form.status = '1';
	form.isBorrowedAccount = '0';
	form.borrowedCustomerId = '';
	form.gamEmailId = '';
	form.hasLink = '0';
	form.reviewAccount = '';
	form.partnerIds = '';

	visible.value = true;
	readonly.value = false;
	await nextTick();

	if (row?.id) {
		await getSiteData(String(row.id));
	} else {
		if (dataFormRef.value) {
			dataFormRef.value.resetFields();
		}
	}
};

// 初始化表单数据
const getSiteData = async (id: string) => {
	loading.value = true;
	try {
		// 先清空邮箱列表和选中值，避免显示旧数据
		gamemail.value = [];
		form.gamEmailId = '';

		await Promise.all([fetchCustomers(), fetchPartners()]);
		const res = await getObj({ id });

		if (!res?.data?.length) {
			useMessage().error('获取数据失败：未找到相关记录');
			return;
		}

		const data = res.data[0];
		const savedGamEmailId = String(data.gamEmailId || ''); // 保存原始的 gamEmailId

		// 先设置基本数据
		form.id = id;
		form.customerId = String(data.customerId);
		form.domain = data.domain;
		form.status = String(data.status);

		form.isBorrowedAccount = String(data.isBorrowedAccount || '0');
		form.borrowedCustomerId = data.borrowedCustomerId ? String(data.borrowedCustomerId) : '';
		form.hasLink = String(data.hasLink || '0');
		form.reviewAccount = data.reviewAccount;
		form.partnerIds = data.partnerIds ? data.partnerIds.split(',') : [];

		// 获取GAM邮箱列表
		const customerId = form.isBorrowedAccount === '1' ? form.borrowedCustomerId : form.customerId;
		if (customerId) {
			try {
				const response = await fetchGamEmailList({ customerId });
				gamemail.value = response.data.records;

				// 验证邮箱ID是否在列表中
				const emailExists = gamemail.value.some((email) => String(email.emailId) === savedGamEmailId);
				if (emailExists) {
					// 确保邮箱列表加载完成后再设置选中值
					await nextTick();
					form.gamEmailId = savedGamEmailId;
				} else {
					console.warn('保存的邮箱ID在当前列表中不存在，将使用第一个邮箱作为默认值');
					await nextTick();
					form.gamEmailId = String(gamemail.value[0].emailId);
				}
			} catch (error) {
				console.error('获取GAM邮箱列表失败:', error);
				gamemail.value = [];
			}
		}
	} catch (error) {
		console.error('获取数据失败:', error);
		useMessage().error('获取数据失败');
	} finally {
		loading.value = false;
	}
};

// 提交
const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
		loading.value = true;
		const submitData = {
			...form,
			customerId: Number(form.customerId),
			isBorrowedAccount: Number(form.isBorrowedAccount),
			hasLink: Number(form.hasLink),
			borrowedCustomerId: form.isBorrowedAccount === '0' ? null : Number(form.borrowedCustomerId),
			partnerIds: Array.isArray(form.partnerIds) ? form.partnerIds.join(',') : form.partnerIds,
		};

		form.id ? await putObj(submitData) : await addObj(submitData);
		useMessage().success(form.id ? '修改成功' : '添加成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg);
	} finally {
		loading.value = false;
	}
};

const customers = ref<Customer[]>([]); // 使用定义的类型
const fetchCustomers = async () => {
	try {
		const response = await fetchCustomerList();
		customers.value = response.data.records as Customer[]; // 假设返回的数据结构中客户列表在`records`字���中
	} catch (error) {
		console.error('Failed to fetch customers:', error);
	}
};
const partners = ref<Partners[]>([]);
const fetchPartners = async () => {
	try {
		const response = await partnerList();
		partners.value = response.data.records as Partners[]; // 假设返回的数据结构中合同列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch partners:', error);
	}
};
const gamemail = ref<GamEmail[]>([]);
const fetchGamEmail = async () => {
	try {
		if (form.isBorrowedAccount === '1' && form.borrowedCustomerId) {
			const response = await fetchGamEmailList({ customerId: form.borrowedCustomerId });
			gamemail.value = response.data.records as GamEmail[];
		} else if (form.isBorrowedAccount === '0' && form.customerId) {
			const response = await fetchGamEmailList({ customerId: form.customerId });
			gamemail.value = response.data.records as GamEmail[];
		}
	} catch (error) {
		console.error('Failed to fetch gamemail:', error);
	}
};
// 暴露变量
defineExpose({
	openDialog,
});
onMounted(() => {
	fetchCustomers();
	fetchPartners();
	fetchGamEmail();
});
console.log('状态字典数据:', yes_no_type.value);

// 添加对是否借用账号的单独监听
watch(
	() => form.isBorrowedAccount,
	(newValue) => {
		if (newValue === '0') {
			// 如果切换为不借用账号，清空借用账号客户
			form.borrowedCustomerId = '';
		}
	}
);

// 修改监听器逻辑
watch([() => form.isBorrowedAccount, () => form.borrowedCustomerId, () => form.customerId], async ([newIsBorrowed, newBorrowedId, newCustomerId]) => {
	// 清空邮箱选择
	form.gamEmailId = '';

	if (newIsBorrowed === '0' && newCustomerId) {
		// 不借用账号，显示当前客户的GAM邮箱
		try {
			const response = await fetchGamEmailList({ customerId: newCustomerId });
			gamemail.value = response.data.records;
		} catch (error) {
			console.error('获取GAM邮箱列表失败:', error);
			gamemail.value = [];
		}
	} else if (newIsBorrowed === '1') {
		if (newBorrowedId) {
			// 借用账号且已选择借用客户，显示借用客户的GAM邮箱
			try {
				const response = await fetchGamEmailList({ customerId: newBorrowedId });
				gamemail.value = response.data.records;
			} catch (error) {
				console.error('获取借用账号GAM邮箱列表失败:', error);
				gamemail.value = [];
			}
		} else {
			// 借用账号但未选择借用客户，清空邮箱列表
			gamemail.value = [];
		}
	} else {
		// 其他情况清空邮箱列表
		gamemail.value = [];
	}
});
</script>
