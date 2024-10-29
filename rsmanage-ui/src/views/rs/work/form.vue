<template>
    <el-dialog :title="form.workId ? '编辑' : '新增'" v-model="visible"
      :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
       <el-row :gutter="24">
    <el-col :span="12" class="mb20">
      <el-form-item label="提交人" prop="submitterName">
        <el-input v-model="form.submitterName" :placeholder="currentUserName" disabled/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="工单标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入工单标题"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
  <el-form-item label="工单内容" prop="content">
    <editor v-if="visible" v-model:get-html="form.content" placeholder="请输入工单内容"></editor>
  </el-form-item>
  </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="工单状态，0:待处理, 1:处理中, 2:已处理" prop="status">
          <el-select v-model="form.status" placeholder="请选择工单状态，0:待处理, 1:处理中, 2:已处理">
            <el-option :value="item.value" :label="item.label" v-for="(item, index) in work_status" :key="index"></el-option>
          </el-select>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="处理人ID" prop="assignees">
        <el-select v-model="form.assignees" multiple placeholder="请选择处理人ID">
          <el-option v-for="user in users" :key="user.userId" :label="user.username" :value="user.userId"></el-option>
        </el-select>
      </el-form-item>
    </el-col>

    <el-col :span="12" class="mb20">
  <el-form-item label="附件路径列表，JSON格式" prop="attachments">
    <upload-file v-model="form.attachments"></upload-file>
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

<script setup lang="ts" name="WorkDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/rs/work'
import { rule } from '/@/utils/validate';
import { pageList } from '/@/api/admin/user';
import { useUserInfo } from '/@/stores/userInfo'; // 引入用户信息

const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false)
const loading = ref(false)
// 定义字典
const { work_status } = useDict('work_status')

// 提交表单数据
const form = reactive({
		workId:'',
	  submitterId: '',
	  submitterName: '', // 添加提交人名称字段
	  title: '',
	  content: '',
	  status: '',
    assignees: [],
	  attachments: '',
});

// 定义校验规则
const dataRules = ref({
    submitterId: [{required: true, message: '提交人ID不能为空', trigger: 'blur'}],
    title: [{required: true, message: '工单标题不能为空', trigger: 'blur'}],
    content: [{required: true, message: '工单内容不能为空', trigger: 'blur'}],
    assignees: [{required: true, message: '处理人ID不能为空', trigger: 'blur'}],
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true;
  form.workId = '';
  form.submitterId = currentUserId; // 设置提交人ID

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

  // 检查attachments字段的值
  if (form.attachments === '') {
    form.attachments = null;
  } else {
    try {
      form.attachments = JSON.parse(form.attachments);
    } catch (e) {
      useMessage().error('附件路径列表格式不正确');
      return false;
    }
  }

	try {
    loading.value = true;
		form.workId ? await putObj(form) : await addObj(form);
		useMessage().success(form.workId ? '修改成功' : '添加成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg);
	} finally {
    loading.value = false;
  }
};


// 初始化表单数据
const getWorkData = (id: string) => {
  // 获取数据
  loading.value = true
  getObj({workId: id}).then((res: any) => {
    Object.assign(form, res.data[0])
  }).finally(() => {
    loading.value = false
  })
};

// 暴露变量
defineExpose({
  openDialog
});

// 获取当前用户信息
const currentUserName = ref('');
const fetchCurrentUser = () => {
  const data = useUserInfo().userInfos;
  currentUserName.value = data.user.name;
  form.submitterName = currentUserName.value;
};

const users = ref([]);

const fetchUsers = async () => {
	try {
		const response = await pageList();
		users.value = response.data.records; // 假设返回的数据结构中用户列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch users:', error);
	}
};

onMounted(() => {
	fetchUsers();
  fetchCurrentUser(); // 获取当前用户信息
});
</script>
