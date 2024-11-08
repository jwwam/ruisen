<template>
	<el-dialog :title="getDialogTitle" v-model="visible" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" v-if="!isFullScreen" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
			<el-row :gutter="24">
				<el-col :span="12" class="mb20">
					<el-form-item label="提交人" prop="submitterName">
						<el-input v-model="form.submitterName" :placeholder="!form.workId ? currentUserName : ''" disabled />
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
							<el-input v-model="form.category" placeholder="请输入自定义分类" @input="updateTitle" :disabled="readonly" />
						</template>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="工单名称" prop="title">
						<el-input v-model="form.title" placeholder="请输入工单标题" :disabled="readonly || !form.workId" />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="工单状态" prop="status">
						<el-select v-model="form.status" placeholder="请选择状态" disabled>
							<el-option v-for="item in work_status" :key="item.value" :label="item.label" :value="item.value"> </el-option>
						</el-select>
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
						<template v-if="readonly">
							<el-input v-model="copyDisplayText" disabled />
						</template>
						<template v-else>
							<el-select v-model="form.copy" multiple placeholder="请选择抄送人" filterable :disabled="readonly">
								<el-option v-for="user in users" :key="user.userId" :label="user.name" :value="user.userId"></el-option>
							</el-select>
						</template>
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="客户名称" prop="customerName">
						<el-input v-model="form.customerName" placeholder="客户名称" disabled />
					</el-form-item>
				</el-col>

				<el-col :span="12" class="mb20">
					<el-form-item label="合作伙伴" prop="partnerCode">
						<el-input v-model="form.partnerCode" placeholder="合作伙伴" disabled />
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
						<upload-file v-model="form.attachments" :disabled="readonly" :showDelete="!readonly" :showUpload="!readonly"></upload-file>
					</el-form-item>
				</el-col>
				<el-col :span="24" class="mb20" v-if="Number(form.status) === 2">
					<el-form-item label="处理意见" prop="handleOpinion">
						<el-input v-model="form.handleOpinion" type="textarea" :rows="4" disabled></el-input>
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
import { getObj, addObj, putObj, validateExist, getWorkDetails } from '/@/api/rs/work';
import { rule } from '/@/utils/validate';
import { pageRoleList } from '/@/api/admin/user';
import { useUserInfo } from '/@/stores/userInfo'; // 引入用户信息

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
// 提交表单数据
const form = reactive({
	workId: '',
	submitterId: 0,
	submitterName: '',
	title: '',
	category: '',
	content: '',
	status: '',
	assignees: '',
	copy: [] as string[],
	customerName: '',
	partnerCode: '',
	deadline: '',
	priority: '',
	attachments: '',
	customCategory: '',
	handleOpinion: '',
});

// 打开弹窗
const openDialog = (id: string, isReadonly: boolean = false) => {
	visible.value = true;
	readonly.value = isReadonly;

	if (id) {
		loading.value = true;
		getWorkDetails({ workId: id })
			.then((res: any) => {
				// 正确获取数据路径
				const workData = res.data.data[0];

				// 打印工单状态相关信息
				console.log('工单状态字典:', work_status.value);
				console.log('后端返回的状态值:', workData.status, typeof workData.status);

				// 将数字转换为字符串后再查找匹配项
				const statusItem = work_status.value.find((item) => item.value === String(workData.status));
				form.status = statusItem ? statusItem.label : '';

				// 将接口返回的数据映射到表单
				form.submitterName = workData.submitterName;
				form.category = workData.category;
				form.title = workData.title;
				form.content = workData.content;
				form.assignees = workData.assignees;
				// 处理抄送人数据
				form.copy = typeof workData.copy === 'string' ? (workData.copy ? workData.copy.split(',') : []) : workData.copy;
				form.customerName = workData.customerName;
				form.partnerCode = workData.partnerCode;
				// 处理日期格式
				if (workData.deadline) {
					// 将字符串转换为Date对象
					form.deadline = new Date(workData.deadline);
				}
				form.priority = workData.priority;
				form.attachments = workData.attachments;
				form.handleOpinion = workData.handleOpinion;

				// 检查是否为自定义分类
				isCustomCategory.value = !['数据缺失', '日报管理', '站点审核', '新通道邀请'].includes(form.category);
			})
			.catch((error) => {
				console.error('获取工单详情失败:', error);
			})
			.finally(() => {
				loading.value = false;
			});
	}
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
	// 只在新增时设置提交人信息
	if (!form.workId) {
		form.submitterName = currentUserName.value;
		form.submitterId = data.user.userId;
	}
};

const users = ref<Users[]>([]);
const fetchUsers = async () => {
	try {
		const response = await pageRoleList();
		users.value = response.data.records; // 假设返回的数据结构中用户列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch users:', error);
	}
};

// 添加新的响应式变量来控制是否显示自定义输入框
const isCustomCategory = ref(false);

onMounted(() => {
	console.log('初始化时的状态字典:', work_status.value);
	fetchUsers();
	fetchCurrentUser(); // 获取当前用户信息
});

const readonly = ref(false);

// 暴露方法
defineExpose({
	openDialog,
});

// 添加计算属性来动态显示对话框题
const getDialogTitle = computed(() => '查看');

const copyDisplayText = computed(() => {
	if (!form.copy) return '';
	// 如果 copy 是字符串（后端返回的逗号分隔的 ID），先转换成数组
	const copyIds = typeof form.copy === 'string' ? form.copy.split(',') : form.copy;
	return copyIds
		.map((id) => users.value.find((user) => user.userId === id)?.name || id)
		.filter(Boolean)
		.join(', ');
});
</script>
