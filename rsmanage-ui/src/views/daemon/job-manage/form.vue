<template>
	<el-dialog v-model="visible" :close-on-click-modal="false" :title="form.jobId ? $t('common.editBtn') : $t('common.addBtn')" draggable>
		<el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="120px" v-loading="loading">
			<el-row :gutter="20">
				<el-col :span="12" class="mb20">
					<el-form-item :label="t('job.configMode')" prop="configMode">
						<el-radio-group v-model="form.configMode">
							<el-radio label="auto">自动配置</el-radio>
							<el-radio label="manual">手动配置</el-radio>
						</el-radio-group>
					</el-form-item>
				</el-col>

				<template v-if="form.configMode === 'auto'">
					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.jobName')" prop="jobName">
							<el-input v-model="form.jobName" :placeholder="t('job.inputjobNameTip')" />
						</el-form-item>
					</el-col>

					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.dataSource')" prop="dataSource">
							<el-select v-model="form.dataSource" :placeholder="t('job.inputDataSourceTip')">
								<el-option label="默认数据源" value="master"></el-option>
								<el-option v-for="ds in datasourceList" :key="ds.id" :label="ds.name" :value="ds.name" />
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.tableName')" prop="tableName">
							<el-select v-model="form.tableName" :placeholder="t('job.inputTableNameTip')" @change="handleTableChange" filterable>
								<el-option v-for="table in tableList" :key="table.name" :label="table.comment || table.name" :value="table.name" />
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.fieldName')" prop="fieldName">
							<el-select v-model="form.fieldName" :placeholder="t('job.inputFieldNameTip')" filterable>
								<el-option
									v-for="field in fieldList"
									:key="field.name"
									:label="field.comment ? `${field.name} (${field.comment})` : field.name"
									:value="field.name"
								/>
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.executeRule')" prop="executeRule">
							<el-select v-model="form.executeRule" :placeholder="t('job.selectExecuteRule')">
								<el-option v-for="item in executeRules" :key="item.value" :label="item.label" :value="item.value" />
							</el-select>
						</el-form-item>
					</el-col>
					<!-- <el-col :span="12" class="mb20">
						<el-form-item :label="t('job.jobType')" prop="jobType">
							<el-select v-model="form.jobType" :placeholder="t('job.jobType')">
								<el-option label="Spring Bean" value="1"></el-option>
							</el-select>
						</el-form-item>
					</el-col> -->

					<el-col :span="12" class="mb20" v-if="['3', '4'].includes(form.jobType)">
						<el-form-item :label="t('job.executePath')" prop="executePath">
							<el-input v-model="form.executePath" :placeholder="t('job.inputexecutePathTip')" />
						</el-form-item>
					</el-col>

					<!-- <el-col :span="12" class="mb20" v-if="['1', '2'].includes(form.jobType)">
						<el-form-item :label="t('job.className')" prop="className">
							<el-input v-model="form.className" :placeholder="t('job.inputclassNameTip')" disabled value="customer" />
						</el-form-item>
					</el-col> -->

					<!-- <el-col :span="12" class="mb20" v-if="['1', '2'].includes(form.jobType)">
						<el-form-item :label="t('job.methodName')" prop="methodName">
							<el-input v-model="form.methodName" :placeholder="t('job.inputmethodNameTip')" />
						</el-form-item>
					</el-col> -->

					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.methodParamsValue')" prop="methodParamsValue">
							<template v-if="form.executeRule === 'between'">
								<el-input-number
									v-model="minValue"
									:placeholder="t('job.minValue')"
									style="width: 45%; margin-right: 5%"
									@change="updateMethodParamsValue"
								/>
								<el-input-number v-model="maxValue" :placeholder="t('job.maxValue')" style="width: 45%" @change="updateMethodParamsValue" />
							</template>
							<el-input v-else v-model="form.methodParamsValue" :placeholder="t('job.inputmethodParamsValueTip')" />
						</el-form-item>
					</el-col>
					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.filterByDate')" prop="filterByDate">
							<el-radio-group v-model="form.filterByDate">
								<el-radio :label="true">是</el-radio>
								<el-radio :label="false">否</el-radio>
							</el-radio-group>
						</el-form-item>
					</el-col>
					<template v-if="form.filterByDate">
						<el-col :span="12" class="mb20">
							<el-form-item :label="t('job.dateField')" prop="dateField">
								<el-select v-model="form.dateField" :placeholder="t('job.selectDateField')" filterable>
									<el-option v-for="field in dateFieldList" :key="field.name" :label="field.comment || field.name" :value="field.name" />
								</el-select>
							</el-form-item>
						</el-col>
						<el-col :span="12" class="mb20">
							<el-form-item :label="t('job.dateRange')" prop="dateRange">
								<el-date-picker
									v-model="dateRange"
									type="daterange"
									range-separator="至"
									start-placeholder="开始日期"
									end-placeholder="结束日期"
									value-format="YYYY-MM-DD"
									style="width: 100%"
								/>
							</el-form-item>
						</el-col>
					</template>

					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.cronExpression')" prop="cronExpression">
							<crontab clearable @hide="popoverVis(false)" v-model="form.cronExpression"></crontab>
						</el-form-item>
					</el-col>

					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.misfirePolicy')" prop="misfirePolicy">
							<el-select v-model="form.misfirePolicy" :placeholder="t('job.inputmisfirePolicyTip')">
								<el-option v-for="(item, index) in misfire_policy" :key="index" :label="item.label" :value="item.value"></el-option>
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.notify')" prop="notify">
							<el-select v-model="form.notify" multiple :placeholder="t('job.selectNotifyUsers')" clearable>
								<el-option v-for="item in userList" :key="item.userId" :label="item.name" :value="item.userId"> </el-option>
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :span="24" class="mb20">
						<el-form-item :label="t('job.remark')" prop="remark">
							<el-input v-model="form.remark" :placeholder="t('job.inputremarkTip')" type="textarea" />
						</el-form-item>
					</el-col>

					<el-col :span="24" class="mb20">
						<el-form-item :label="t('job.sqlPreview')" prop="sqlPreview">
							<el-input v-model="sqlPreview" type="textarea" :rows="3" readonly :placeholder="t('job.sqlPreviewTip')" />
						</el-form-item>
					</el-col>
				</template>

				<template v-else>
					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.jobName')" prop="jobName">
							<el-input v-model="form.jobName" :placeholder="t('job.inputjobNameTip')" />
						</el-form-item>
					</el-col>

					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.cronExpression')" prop="cronExpression">
							<crontab clearable @hide="popoverVis(false)" v-model="form.cronExpression"></crontab>
						</el-form-item>
					</el-col>

					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.misfirePolicy')" prop="misfirePolicy">
							<el-select v-model="form.misfirePolicy" :placeholder="t('job.inputmisfirePolicyTip')">
								<el-option v-for="dict in misfire_policy" :key="dict.value" :label="dict.label" :value="dict.value" />
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :span="12" class="mb20">
						<el-form-item :label="t('job.notify')" prop="notify">
							<el-select v-model="form.notify" multiple :placeholder="t('job.selectNotifyUsers')" clearable>
								<el-option v-for="item in userList" :key="item.userId" :label="item.name" :value="item.userId"> </el-option>
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :span="24" class="mb20">
						<el-form-item :label="t('job.remark')" prop="remark">
							<el-input v-model="form.remark" :placeholder="t('job.inputremarkTip')" type="textarea" />
						</el-form-item>
					</el-col>

					<el-col :span="24" class="mb20">
						<el-form-item :label="t('job.sqlPreview')" prop="sqlPreview">
							<el-input
								v-model="sqlPreview"
								type="textarea"
								:rows="3"
								:readonly="form.configMode === 'auto'"
								:placeholder="form.configMode === 'auto' ? t('job.sqlPreviewTip') : t('job.inputSqlTip')"
							/>
						</el-form-item>
					</el-col>
				</template>
			</el-row>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button formDialogRef @click="visible = false">{{ $t('common.cancelButtonText') }}</el-button>
				<el-button formDialogRef type="primary" @click="onSubmit" :disabled="loading">{{ $t('common.confirmButtonText') }}</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script lang="ts" name="SysJobDialog" setup>
// 定义子组件向父组件传值/事件
import { useDict } from '/@/hooks/dict';
import { useMessage } from '/@/hooks/message';
import { addObj, getObj, putObj } from '/@/api/daemon/job';
import { useI18n } from 'vue-i18n';
import { rule } from '/@/utils/validate';
import { pageSalesRepList } from '/@/api/admin/user';
import { list } from '/@/api/gen/datasource';
import { fetchList, useTableApi } from '/@/api/gen/table';
const emit = defineEmits(['refresh']);
const Crontab = defineAsyncComponent(() => import('/@/components/Crontab/index.vue'));

const { t } = useI18n();

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);

// 定义字典
const { misfire_policy, job_type } = useDict('job_status', 'job_execute_status', 'misfire_policy', 'job_type');

// 提交表单数据
const form = reactive({
	jobId: '',
	jobName: '',
	jobGroup: '',
	jobType: '1',
	executePath: '',
	className: 'customer',
	methodName: 'customerTask',
	methodParamsValue: '',
	cronExpression: '',
	misfirePolicy: '',
	jobStatus: '',
	jobExecuteStatus: '',
	remark: '',
	notify: [],
	dataSource: '',
	tableName: '',
	fieldName: '',
	executeRule: '',
	filterByDate: false,
	dateField: '',
	configMode: 'auto',
});

const popoverVis = (bol: boolean) => {
	popoverVisible.value = bol;
};

const popoverVisible = ref(false);
// 定义校验规则
const dataRules = reactive({
	jobName: [
		{ validator: rule.overLength, trigger: 'blur' },
		{ required: true, message: '任务名称不能为空', trigger: 'blur' },
	],
	jobGroup: [
		{ validator: rule.overLength, trigger: 'blur' },
		{ required: true, message: '任务组名不能为空', trigger: 'blur' },
	],
	jobType: [{ required: true, message: '任务类型不能为空', trigger: 'blur' }],
	cronExpression: [
		{ validator: rule.overLength, trigger: 'blur' },
		{ required: true, message: 'cron不能为空', trigger: 'blur' },
	],
	misfirePolicy: [{ required: true, message: '策略不能为空', trigger: 'blur' }],
	executePath: [
		{ validator: rule.overLength, trigger: 'blur' },
		{ required: true, message: '执行路径不能为空', trigger: 'blur' },
	],
	className: [
		{ validator: rule.overLength, trigger: 'blur' },
		{ required: true, message: '执行文件不能为空', trigger: 'blur' },
	],
	methodName: [
		{ validator: rule.overLength, trigger: 'blur' },
		{ required: true, message: '执行方法不能为空', trigger: 'blur' },
	],
	methodParamsValue: [
		{ validator: rule.overLength, trigger: 'blur' },
		{ required: true, message: '参数值不能为空', trigger: 'blur' },
	],
	notify: [{ required: true, message: '告警对象不能为空', trigger: 'blur' }],
	dataSource: [
		{ validator: rule.overLength, trigger: 'blur' },
		{ required: true, message: '数据源不能为空', trigger: 'blur' },
	],
	tableName: [
		{ validator: rule.overLength, trigger: 'blur' },
		{ required: true, message: '表名不能为空', trigger: 'blur' },
	],
	fieldName: [
		{ validator: rule.overLength, trigger: 'blur' },
		{ required: true, message: '字段名不能为空', trigger: 'blur' },
	],
	executeRule: [{ required: true, message: '执行规则不能为空', trigger: 'blur' }],
});

// 打开弹窗
const openDialog = (id: string) => {
	visible.value = true;

	// 重置所有表单数据
	Object.assign(form, {
		jobId: '',
		jobName: '',
		jobGroup: '',
		jobType: '1',
		executePath: '',
		className: 'customer',
		methodName: 'customerTask',
		methodParamsValue: '',
		cronExpression: '',
		misfirePolicy: '',
		jobStatus: '',
		jobExecuteStatus: '',
		remark: '',
		notify: [],
		dataSource: '',
		tableName: '',
		fieldName: '',
		executeRule: '',
		filterByDate: false,
		dateField: '',
		configMode: 'auto',
		sqlPreview: '',
	});

	// 重置其他相关数据
	dateRange.value = [];
	tableList.value = [];
	fieldList.value = [];
	minValue.value = '';
	maxValue.value = '';

	getUserListData();
	getDatasourceList();

	// 重置表单校验
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

	// 获取sysJob信息
	if (id) {
		form.jobId = id;
		getsysJobData(id);
	}
};

// 提交
const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
		loading.value = true;
		// 根据配置模式准备不同的提交数据
		const baseData = {
			jobId: form.jobId,
			jobName: form.jobName,
			jobType: '2',
			executePath: '',
			jobStatus: form.jobId ? form.jobStatus : '1', // 编辑时保留原值，新增时默认为'1'
			className: form.className, // 添加 className
			methodName: form.methodName, // 添加 methodName
			jobGroup: 'DEFAULT', // 添加默认的任务组
			cronExpression: form.cronExpression,
			misfirePolicy: form.misfirePolicy,
			notify: form.notify.join(','),
			remark: form.remark,
			configMode: form.configMode,
		};

		let submitData;
		if (form.configMode === 'auto') {
			// 自动模式的额外字段
			submitData = {
				...baseData,
				dataSource: form.dataSource,
				tableName: form.tableName,
				fieldName: form.fieldName,
				executeRule: form.executeRule,
				methodParamsValue: form.methodParamsValue,
				filterByDate: form.filterByDate,
				dateField: form.dateField,
				sqlPreview: sqlPreview.value,
			};
		} else {
			// 手动模式只需要基础字段和SQL
			submitData = {
				...baseData,
				sqlPreview: sqlPreview.value,
			};
		}

		form.jobId ? await putObj(submitData) : await addObj(submitData);
		useMessage().success(t(form.jobId ? 'common.editSuccessText' : 'common.addSuccessText'));
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		// 修改错误处理逻辑
		if (err.msg?.includes('uk_method_name') || err.message?.includes('uk_method_name')) {
			useMessage().error('该方法名已存在，请更换其他方法名');
		} else {
			useMessage().error(err.msg || '任务初始化异常');
		}
	} finally {
		loading.value = false;
	}
};

// 初始化表单数据
const getsysJobData = async (id: string) => {
	try {
		const res = await getObj(id);
		const data = { ...res.data };
		// 设置通知人
		data.notify = data.notify ? data.notify.split(',') : [];

		// 先设置数据源，触发表列表加载
		form.dataSource = data.dataSource;
		await nextTick();

		// 等待表列表加载完成后设置表名
		await getTableList(data.dataSource);
		form.tableName = data.tableName;
		await nextTick();

		// 等待字段列表加载完成后设置字段名
		await getFieldList(data.dataSource, data.tableName);

		// 如果是 between 规则，解析最大最小值
		if (data.executeRule === 'between' && data.methodParamsValue) {
			const [min, max] = data.methodParamsValue.split(',');
			minValue.value = min;
			maxValue.value = max;
		}

		// 设置日期范围
		if (data.sqlPreview) {
			const dateMatch = data.sqlPreview.match(/(\w+) BETWEEN '(\d{4}-\d{2}-\d{2})' AND '(\d{4}-\d{2}-\d{2})'/);
			if (dateMatch) {
				const [, field, startDate, endDate] = dateMatch;
				// 如果匹配到的字段在日期字段列表中，则设置相关值
				if (dateFieldList.value.some((f) => f.name === field)) {
					form.filterByDate = true;
					form.dateField = field;
					dateRange.value = [startDate, endDate];
				}
			}
		}

		// 最后设置所有表单数据
		Object.assign(form, data);
	} catch (error) {
		console.error('获取任务详情失败:', error);
		useMessage().error('获取任务详情失败');
	}
};

// 获取用户列表
const userList = ref([]);

// 获取用户列表
const getUserListData = async () => {
	try {
		const res = await pageSalesRepList();
		userList.value = res.data.records;
	} catch (error) {
		console.error('获取用户列表失败:', error);
	}
};

// 添加执行规则选项
const executeRules = [
	{ label: 'like', value: 'like' },
	{ label: '=', value: '=' },
	{ label: '>', value: '>' },
	{ label: '<', value: '<' },
	{ label: 'between', value: 'between' },
];

// 数据源列表
const datasourceList = ref([]);

// 获取数据源列表
const getDatasourceList = async () => {
	try {
		const res = await list();
		datasourceList.value = res.data;
	} catch (error) {
		console.error('获取数据源列表失败:', error);
	}
};

// 表列表
const tableList = ref([]);

// 获取表列表
const getTableList = async (dsName: string) => {
	try {
		const res = await fetchList({
			dsName: dsName,
			current: 1,
			size: 999,
		});

		if (res.data && res.data.records) {
			tableList.value = res.data.records
				.filter((item) => item.metadata.TABLE_NAME.startsWith('rs_')) // 只保留rs_开头的表
				.map((item) => ({
					name: item.metadata.TABLE_NAME,
					comment: item.metadata.TABLE_COMMENT,
				}));
		}
	} catch (error) {
		console.error('获取表列表失败:', error);
	}
};

// 监听数据源变化
watch(
	() => form.dataSource,
	(newValue) => {
		if (newValue) {
			getTableList(newValue);
			form.tableName = '';
			form.fieldName = '';
		}
	}
);

// 添加字段列表的响应式变量
const fieldList = ref([]);

// 添加获取字段列表的方法
const getFieldList = async (dsName: string, tableName: string) => {
	try {
		const res = await useTableApi(dsName, tableName);
		if (res.data && res.data.fieldList) {
			// 所有字段列表
			fieldList.value = res.data.fieldList.map((item) => ({
				name: item.fieldName,
				comment: item.fieldComment,
			}));

			// 时间字段列表（TIMESTAMP和DATE类型）
			dateFieldList.value = res.data.fieldList
				.filter((item) => item.fieldType.toUpperCase().includes('TIMESTAMP') || item.fieldType.toUpperCase().includes('DATE'))
				.map((item) => ({
					name: item.fieldName,
					comment: item.fieldComment,
				}));
		}
	} catch (error) {
		console.error('获取字段列表失败:', error);
	}
};

// 修改表名变化的处理方法
const handleTableChange = (value: string) => {
	form.fieldName = ''; // 清空字段名
	if (value) {
		getFieldList(form.dataSource, value);
	}
};

// 添加日期范围的响应式变量
const dateRange = ref([]);

// 修改 SQL 预览的计算属性
const sqlPreview = computed({
	get: () => {
		if (form.configMode === 'manual') {
			return form.sqlPreview || '';
		}

		const { tableName, fieldName, executeRule, methodParamsValue, filterByDate, dateField } = form;
		if (!tableName || !fieldName || !executeRule) return '';

		let sql = `SELECT * FROM ${tableName} WHERE `;

		// 主要条件
		switch (executeRule) {
			case 'like':
				sql += `${fieldName} LIKE '%${methodParamsValue}%'`;
				break;
			case 'between':
				const [start, end] = methodParamsValue.split(',');
				sql += `${fieldName} BETWEEN '${start}' AND '${end}'`;
				break;
			default:
				sql += `${fieldName} ${executeRule} '${methodParamsValue}'`;
		}

		// 只有当filterByDate为true且有日期范围和日期字段时才添加日期条件
		if (filterByDate && dateRange.value && dateRange.value.length === 2 && dateField) {
			sql += ` AND ${dateField} BETWEEN '${dateRange.value[0]}' AND '${dateRange.value[1]}'`;
		}

		return sql;
	},
	set: (value) => {
		if (form.configMode === 'manual') {
			form.sqlPreview = value;
		}
	},
});

// 添加最小值和最大值的响应式变量
const minValue = ref('');
const maxValue = ref('');

// 更新methodParamsValue的方法
const updateMethodParamsValue = () => {
	if (form.executeRule === 'between') {
		form.methodParamsValue = `${minValue.value},${maxValue.value}`;
	}
};

// 监听executeRule的变化
watch(
	() => form.executeRule,
	(newValue) => {
		if (newValue === 'between') {
			// 如果已有methodParamsValue，则解析其值
			if (form.methodParamsValue) {
				const [min, max] = form.methodParamsValue.split(',');
				minValue.value = min;
				maxValue.value = max;
			} else {
				minValue.value = '';
				maxValue.value = '';
			}
		}
	}
);

// 添加时间字段列表
const dateFieldList = ref([]);

// 监听 filterByDate 变化，当切换为否时清空相关字段
watch(
	() => form.filterByDate,
	(newValue) => {
		if (!newValue) {
			form.dateField = '';
			dateRange.value = [];
		}
	}
);

// 暴露变量
defineExpose({
	openDialog,
});
</script>
