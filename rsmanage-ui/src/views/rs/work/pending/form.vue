<template>
	<el-dialog title="查看" v-model="visible" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="form" :rules="dataRules" label-width="90px" v-loading="loading">
			<el-row :gutter="24">
				<el-col :span="12" class="mb20">
					<el-form-item label="提交人" prop="submitterName">
						<el-input v-model="form.submitterName" disabled />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="工单分类" prop="category">
						<el-select v-model="form.category" disabled>
							<el-option label="数据缺失" value="数据缺失"></el-option>
							<el-option label="日报管理" value="日报管理"></el-option>
							<el-option label="站点审核" value="站点审核"></el-option>
							<el-option label="新通道邀请" value="新通道邀请"></el-option>
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="工单名称" prop="title">
						<el-input v-model="form.title" disabled />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="工单状态" prop="status">
						<el-input v-model="statusText" disabled />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="处理人" prop="assignees">
						<el-select v-model="form.assignees" disabled>
							<el-option v-for="user in users" :key="user.userId" :label="user.name" :value="user.userId"></el-option>
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="抄送人" prop="copy">
						<el-input v-model="copyDisplayText" disabled />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="客户名称" prop="customerId">
						<el-select v-model="form.customerId" disabled>
							<el-option :label="form.customerName" :value="form.customerId"> </el-option>
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="合作伙伴" prop="partnerId">
						<el-select v-model="form.partnerId" disabled>
							<el-option :label="form.partnerCode" :value="form.partnerId"> </el-option>
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="截止日期" prop="deadline">
						<el-date-picker v-model="form.deadline" type="date" disabled />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="优先级" prop="priority">
						<el-select v-model="form.priority" disabled>
							<el-option label="紧急" value="紧急"></el-option>
							<el-option label="一般" value="一般"></el-option>
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="24" class="mb20">
					<el-form-item label="工单内容" prop="content">
						<el-input v-model="form.content" type="textarea" :rows="4" disabled />
					</el-form-item>
				</el-col>
				<el-col :span="24" class="mb20" v-if="form.status !== 0 && form.status !== 3">
					<el-form-item label="处理意见" prop="handleOpinion">
						<el-input
							v-model="form.handleOpinion"
							type="textarea"
							:rows="4"
							:disabled="form.status === 2"
							:placeholder="form.status === 1 ? '请输入处理意见' : ''"
						/>
					</el-form-item>
				</el-col>
				<el-col :span="24" class="mb20">
					<el-form-item label="附件：" prop="attachments">
						<div v-if="form.attachmentsList && form.attachmentsList.length">
							<div v-for="file in form.attachmentsList" :key="file.id" class="file-item">
								<el-link type="primary" @click="handleFileDownload(file)">
									{{ file.original }}
								</el-link>
							</div>
						</div>
						<div v-else>无附件</div>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="visible = false">关 闭</el-button>
				<el-button v-if="form.status === 0 || form.status === 1" type="primary" @click="handleMarkStatus" :loading="loading">
					{{ form.status === 0 ? '标记为处理中' : '标记为已处理' }}
				</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script setup lang="ts" name="WorkDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from '/@/hooks/message';
import { getObj, addObj, putObj, validateExist, getWorkDetails } from '/@/api/rs/work';
import { rule } from '/@/utils/validate';
import { pageRoleList } from '/@/api/admin/user';
import { useUserInfo } from '/@/stores/userInfo'; // 引入用户信息
import { fetchListWithoutRole as fetchCustomerList } from '/@/api/rs/customers'; // 引入客户表信息
import { fetchListWithoutRole as fetchPartnerList } from '/@/api/rs/partners'; // 引入合作伙伴表信息

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
	status: 0,
	assignees: '',
	copy: [] as string[],
	customerId: '',
	customerName: '',
	partnerId: '',
	partnerCode: '',
	deadline: '',
	priority: '',
	customCategory: '',
	handleOpinion: '', //处理意见
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
	handleOpinion: [
		{
			required: true,
			message: '处理意见不能为空',
			trigger: 'blur',
			validator: (rule: any, value: any, callback: any) => {
				if (form.status === 1 && (!value || value.trim() === '')) {
					callback(new Error('处理意见不能为空'));
				} else {
					callback();
				}
			},
		},
	],
});

const formDisabled = ref(true);

// 打开弹窗
const openDialog = (id: string) => {
	visible.value = true;
	form.workId = '';
	formDisabled.value = true;
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

// 初始化表单
const getWorkData = (id: string) => {
	loading.value = true;
	getWorkDetails({ workId: id })
		.then((res: any) => {
			const workData = res.data.data[0];
			console.log('workData:', workData);
			Object.assign(form, workData);
		})
		.finally(() => {
			loading.value = false;
		});
};

// 获取当前用户信息
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
		const response = await fetchPartnerList();
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

// 暴露方法
defineExpose({
	openDialog,
});

// 标记状态
const handleMarkStatus = async () => {
	try {
		loading.value = true;
		let targetStatus;

		if (form.status === 0) {
			targetStatus = Number(work_status.value.find((item) => item.label === '处理中')?.value);
		} else if (form.status === 1) {
			const valid = await dataFormRef.value.validateField('handleOpinion');
			if (!valid) {
				loading.value = false;
				return;
			}
			targetStatus = Number(work_status.value.find((item) => item.label === '已处理')?.value);
		}

		await putObj({
			workId: form.workId,
			status: targetStatus,
			handleOpinion: form.handleOpinion,
		});

		loading.value = false;
		visible.value = false;
		emit('refresh');
	} catch (error) {
		console.error('Failed to update status:', error);
		loading.value = false;
	}
};

const statusText = computed(() => {
	const statusMap: { [key: number]: string } = {
		0: '待处理',
		1: '处理中',
		2: '已处理',
		3: '已终止',
	};
	return statusMap[form.status] || form.status;
});
const copyDisplayText = computed(() => {
	if (!form.copy) return '';
	// 如果 copy 是字符串（后端返回的逗号分隔的 ID），先转换成数组
	const copyIds = typeof form.copy === 'string' ? form.copy.split(',') : form.copy;
	return copyIds
		.map((id) => users.value.find((user) => user.userId === id)?.name || id)
		.filter(Boolean)
		.join(', ');
});

// 获取文件名
const getFileName = (url: string) => {
	return url.substring(url.lastIndexOf('/') + 1);
};

// 获取 table 相关方法
const { downBlobFile } = useTable({
	// 可以传入基础配置，如果没有特殊需求可以传空对象
	queryForm: {},
	dataList: [],
});

// 添加下载方法
const handleFileDownload = async (file: any) => {
	try {
		const url = `/admin/sys-file/${file.bucketName}/${file.fileName}`;
		const query = {};
		await downBlobFile(url, query, file.original);
	} catch (error) {
		console.error('文件下载失败:', error);
	}
};
</script>
