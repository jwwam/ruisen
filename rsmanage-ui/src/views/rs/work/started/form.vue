<template>
	<el-dialog :title="getDialogTitle" v-model="visible" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" v-if="!isFullScreen" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
			<el-row :gutter="24">
				<el-col :span="12" class="mb20">
					<el-form-item label="提交人" prop="submitterName">
						<el-input v-model="form.submitterName" :placeholder="currentUserName" disabled />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="工单分类" prop="category">
						<template v-if="!isCustomCategory">
							<el-select v-model="form.category" placeholder="请选择工单分类" filterable @change="handleCategoryChange" :disabled="readonly">
								<el-option label="数据缺失" value="数据缺失"></el-option>
								<el-option label="日报管理" value="日报管理"></el-option>
								<el-option label="站点审核" value="站点审核"></el-option>
								<el-option label="新通道邀请" value="新通道邀请"></el-option>
								<el-option label="自定义" value="自定义"></el-option>
							</el-select>
						</template>
						<template v-else>
							<el-input v-model="form.category" placeholder="请输入自定义分类" @input="updateTitle" :disabled="readonly"/>
						</template>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="工单名称" prop="title">
						<el-input v-model="form.title" placeholder="请输入工单标题" :disabled="readonly" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="工单状态" prop="status">
						<el-input v-model="form.status" disabled />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="处理人" prop="assignees">
						<el-select v-model="form.assignees" placeholder="请选择处理人" filterable :disabled="readonly">
							<el-option v-for="user in users" :key="user.userId" :label="user.name" :value="user.userId"></el-option>
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="抄送人" prop="copy">
						<el-select v-model="form.copy" multiple placeholder="请选择抄送人" filterable :disabled="readonly">
							<el-option v-for="user in users" :key="user.userId" :label="user.name" :value="user.userId"></el-option>
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="客户名称" prop="customerId">
						<el-select v-model="form.customerId" placeholder="请选择客户" filterable :disabled="readonly">
							<el-option v-for="customer in customers" :key="customer.customerId" :label="customer.name" :value="customer.customerId"></el-option>
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="合作伙伴" prop="partnerId">
						<el-select v-model="form.partnerId" placeholder="请选择合作伙伴" filterable :disabled="readonly">
							<el-option v-for="partner in partners" :key="partner.partnerId" :label="partner.partnerCode" :value="partner.partnerId"></el-option>
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="截止日期" prop="deadline">
						<el-date-picker v-model="form.deadline" type="date" placeholder="选择截止日期" :disabled="readonly"></el-date-picker>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="优先级" prop="priority">
						<el-select v-model="form.priority" placeholder="请选择优先级" :disabled="readonly">
							<el-option label="紧急" value="紧急"></el-option>
							<el-option label="一般" value="一般"></el-option>
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="24" class="mb20">
					<el-form-item label="工单内容" prop="content">
						<el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入工单内容" :disabled="readonly"></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="24" class="mb20">
					<el-form-item label="附件上传" prop="attachments">
						<upload-file v-model="form.attachments" :disabled="readonly"></upload-file>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
		<template #footer>
			<span class="dialog-footer" v-if="!readonly">
				<el-button @click="visible = false">取 消</el-button>
				<el-button type="primary" @click="onSubmit" :disabled="loading">确 认</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script setup lang="ts" name="WorkDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from '/@/hooks/message';
import { getObj, addObj, putObj, validateExist } from '/@/api/rs/work';
import { rule } from '/@/utils/validate';
import { pageRoleList } from '/@/api/admin/user';
import { useUserInfo } from '/@/stores/userInfo'; // 引入用户信息
import { fetchList as fetchCustomerList } from '/@/api/rs/customers'; // 引入客户表信息
import { fetchList as partnerList } from '/@/api/rs/partners'; // 引入合作伙伴表信息

const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
// 定义字典
const { work_status } = useDict('work_status');
// 定义对象的类型
interface Users {
	isAdmin: string;
	userId: string;
	name: string;
}
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
	workId: '',
	submitterId: 0,
	submitterName: '',
	title: '',
	category: '',
	content: '',
	status: '待处理' as any,
	assignees: '',
	copy: [] as string[],
	customerId: '',
	partnerId: '',
	deadline: '',
	priority: '',
	attachments: '',
	customCategory: '',
});

// 定义校验规则
const dataRules = ref({
	submitterId: [{ required: true, message: '提交人ID不能为空', trigger: 'blur' }],
	title: [{ required: true, message: '工单标题不能为空', trigger: 'blur' }],
	category: [{ required: true, message: '工单分类不能为空', trigger: 'blur' }],
	content: [{ required: true, message: '工单内容不能为空', trigger: 'blur' }],
	assignees: [{ required: true, message: '处理人ID不能为空', trigger: 'blur' }],
	deadline: [{ required: true, message: '截止日期不能为空', trigger: 'blur' }],
	priority: [{ required: true, message: '优先级不能为空', trigger: 'blur' }],
});

// 打开弹窗
const openDialog = (id: string, isReadonly: boolean = false) => {
	visible.value = true;
	form.workId = '';
	readonly.value = isReadonly; // 设置只读状态

	// 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

	// 获取work信息
	if (id) {
		form.workId = id;
		getWorkData(id);
	}
};
// 提交
const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	// 在提交前将 status 转换为整数
	const originalStatus = form.status;
	form.status = originalStatus === '待处理' ? 0 : form.status;

	// 处理抄送人
	const ccString = form.copy.join(',');

	// 格式化 deadline
	let formattedDeadline = '';
	if (form.deadline) {
		const date = new Date(form.deadline);
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, '0');
		const day = String(date.getDate()).padStart(2, '0');
		formattedDeadline = `${year}${month}${day}`;
	}

	try {
		loading.value = true;
		const payload = {
			...form,
			copy: ccString,
			deadline: formattedDeadline,
			// attachments 直接使用原值，为空时设为 null
			attachments: form.attachments || null,
		};

		form.workId ? await putObj(payload) : await addObj(payload);
		useMessage().success(form.workId ? '修改成功' : '添加成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg || '操作失败');
	} finally {
		loading.value = false;
		form.status = originalStatus;
	}
};

// 初始化表单数据
const getWorkData = (id: string) => {
	loading.value = true;
	getObj({ workId: id })
		.then((res: any) => {
			Object.assign(form, res.data[0]);
			// 检查是否为自定义分类
			isCustomCategory.value = !['数据缺失', '日报管理', '站点审核', '新通道邀请'].includes(form.category);
		})
		.finally(() => {
			loading.value = false;
		});
};

// 暴露变量
// defineExpose({
// 	openDialog,
// });

// 获取当前用户信息
const currentUserId = ref('');
const currentUserName = ref('');
const fetchCurrentUser = () => {
	const data = useUserInfo().userInfos;
	currentUserName.value = data.user.name;
	form.submitterName = currentUserName.value;
	form.submitterId = data.user.userId;
};

const users = ref<Users[]>([]);
const customers = ref<Customer[]>([]); // 使用定义的类型
const partners = ref<Partners[]>([]);
const fetchUsers = async () => {
	try {
		const response = await pageRoleList();
		users.value = response.data.records; // 假设返回的数据结构中用户列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch users:', error);
	}
};

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

// 更新工单名称
const updateTitle = () => {
	form.title = `${form.submitterName}-${form.category}`;
};

// 添加新的响应式变量来控制是否显示自定义输入框
const isCustomCategory = ref(false);

// 处理分类变化
const handleCategoryChange = (value: string) => {
	if (value === '自定义') {
		isCustomCategory.value = true;
		form.category = ''; // 清空category以便输入
	} else {
		isCustomCategory.value = false;
		updateTitle();
	}
};

onMounted(() => {
	fetchUsers();
	fetchCurrentUser(); // 获取当前用户信息
	updateTitle(); // 初始化时更新工单名称
	fetchCustomers();
	fetchPartners();
});

const readonly = ref(false);

// 暴露方法
defineExpose({
	openDialog
});

// 添加计算属性来动态显示对话框标题
const getDialogTitle = computed(() => {
	if (readonly.value) {
		return '查看';
	}
	return form.workId ? '编辑' : '新增';
});
</script>
