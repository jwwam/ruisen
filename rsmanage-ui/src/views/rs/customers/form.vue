<template>
	<el-dialog :title="form.customerId ? '编辑' : '新增'" v-model="visible" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
			<el-row :gutter="24">
				<el-col :span="12" class="mb20">
					<el-form-item label="客户名称" prop="name">
						<el-input v-model="form.name" placeholder="请输入客户名称" />
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="我方商务" prop="salesRepId">
						<el-select v-model="form.salesRepId" placeholder="请选择我方商务人员" filterable>
							<el-option v-for="user in users" :key="user.userId" :label="user.name" :value="user.name" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="联系人" prop="linkman">
						<el-input v-model="form.linkman" placeholder="请输入联系人" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="客户邮箱" prop="email">
						<el-input v-model="form.email" placeholder="请输入客户电子邮件" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="客户电话" prop="phoneNumber">
						<el-input v-model="form.phoneNumber" placeholder="请输入客户电话号码" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="客户主体" prop="companyName">
						<el-input v-model="form.companyName" placeholder="请输入公司名称" />
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="财务人员" prop="financeContactUser">
						<el-input v-model="form.financeContactUser" placeholder="请输入客户财务人员" />
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="财务邮箱" prop="financeEmail">
						<el-input v-model="form.financeEmail" placeholder="请输入客户财务人员邮箱" />
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="财务电话" prop="financePhone">
						<el-input v-model="form.financePhone" placeholder="请输入客户财务人员电话" />
					</el-form-item>
				</el-col>
				<el-col :span="12" class="mb20">
					<el-form-item label="我方客户" prop="isOurCustomer">
						<el-radio-group v-model="form.isOurCustomer">
							<el-radio :label="1">是</el-radio>
							<el-radio :label="0">否</el-radio>
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

<script setup lang="ts" name="CustomersDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from '/@/hooks/message';
import { getObj, addObj, putObj, validateExist } from '/@/api/rs/customers';
import { rule } from '/@/utils/validate';
import { pageSalesRepList } from '/@/api/admin/user';
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);

// 定义对象的类型
interface Users {
	isAdmin: string;
	userId: string;
	name: string;
}
// 提交表单数据
const form = reactive({
	customerId: '',
	name: '',
	linkman: '',
	email: '',
	phoneNumber: '',
	salesRepId: '',
	companyName: '',
	isOurCustomer: 0,
	financeContactUser: '',
	financeEmail: '',
	financePhone: '',
});

// 定义校验规则
const dataRules = ref({
	name: [{ required: true, message: '客户名称不能为空', trigger: 'blur' }],
	linkman: [{ required: true, message: '联系人不能为空', trigger: 'blur' }],
	email: [
		{ required: true, message: '客户邮箱不能为空', trigger: 'blur' },
		{ validator: rule.email, trigger: 'blur' },
	],
	phoneNumber: [{ required: true, message: '客户电话不能为空', trigger: 'blur' }],
	companyName: [{ required: true, message: '客户主体不能为空', trigger: 'blur' }],
	salesRepId: [{ required: true, message: '商务id不能为空', trigger: 'blur' }],
	isOurCustomer: [{ required: true, message: '请选择是否为我方客户', trigger: 'change' }],
});

// 打开弹窗
const openDialog = (id: string) => {
	visible.value = true;
	form.customerId = '';

	// 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

	// 获取customers信息
	if (id) {
		form.customerId = id;
		getCustomersData(id);
	}
};

// 提交
const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
		loading.value = true;
		// 检查三个财务字段是否都为空
		if (!form.financeContactUser && !form.financeEmail && !form.financePhone) {
			// 如果都为空，则复用联系人信息
			form.financeContactUser = form.linkman;
			form.financeEmail = form.email;
			form.financePhone = form.phoneNumber;
		}

		form.customerId ? await putObj(form) : await addObj(form);
		useMessage().success(form.customerId ? '修改成功' : '添加成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg);
	} finally {
		loading.value = false;
	}
};

// 初始化表单数据
const getCustomersData = (id: string) => {
	// 获取数据
	loading.value = true;
	getObj({ customerId: id })
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

const users = ref<Users[]>([]);

const fetchUsers = async () => {
	try {
		const response = await pageSalesRepList();
		users.value = response.data.records; // 假设返回的数据结构中用户列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch users:', error);
	}
};

onMounted(() => {
	fetchUsers();
});
</script>
