<template>
    <el-dialog :title="form.partnerId ? '编辑' : '新增'" v-model="visible"
      :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
       <el-row :gutter="24">
    <el-col :span="12" class="mb20">
      <el-form-item label="合作伙伴名称" prop="partnerName" class="no-wrap-label">
        <el-input v-model="form.partnerName" placeholder="请输入合作伙伴名称"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="合作伙伴标识" prop="partnerCode" class="no-wrap-label">
        <el-input v-model="form.partnerCode" placeholder="请输入合作伙伴标识"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="合作开始时间" prop="startDate" class="no-wrap-label">
      <el-date-picker type="date" placeholder="请选择合作开始时间" v-model="form.startDate" :value-format="dateStr"></el-date-picker>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="合作结束时间" prop="endDate" class="no-wrap-label">
      <el-date-picker type="date" placeholder="请选择合作结束时间" v-model="form.endDate" :value-format="dateStr"></el-date-picker>
      </el-form-item>
      </el-col>
    <el-col :span="12" class="mb20">
      <el-form-item label="分成有效月数" prop="validMonths" class="no-wrap-label">
        <el-select v-model="form.validMonths" placeholder="请选择分成有效月数" filterable allow-create default-first-option>
          <el-option v-for="month in 12" :key="month" :label="month + '个月'" :value="month"/>
          <el-option label="无限制" value="9999"/>
        </el-select>
      </el-form-item>
    </el-col>

    <!-- <el-col :span="12" class="mb20">
      <el-form-item label="分成比例" prop="revenueShare" class="no-wrap-label">
        <el-input-number :min="1" :max="1000" v-model="form.revenueShare" placeholder="请输入分成比例"></el-input-number>
      </el-form-item>
    </el-col> -->

			</el-row>

        <div class="revenue-shares-section">
          <div class="section-header">
            <h3>分成比例</h3>
          </div>

          <div v-for="(item, index) in form.revenueShares" :key="index" class="share-item">
            <div class="share-item-header">
              <h4>分成比例{{ index + 1 }}</h4>
              <el-button type="danger" link @click="removeRevenueShare(item.shareId)">
                <el-icon><Delete /></el-icon>删除
              </el-button>
            </div>
            <el-row :gutter="24">
              <el-col :span="12" class="mb20">
                <el-form-item :label="'上游分成比例'" :prop="'revenueShares.' + index + '.higherShare'" class="no-wrap-label">
                  <el-input-number v-model="item.higherShare" :min="1" :max="100" placeholder="请输入上游分成比例"/>
                </el-form-item>
              </el-col>
              <el-col :span="12" class="mb20">
                <el-form-item :label="'分成比例'" :prop="'revenueShares.' + index + '.share'" class="no-wrap-label">
                  <el-input-number v-model="item.share" :min="1" :max="100" placeholder="请输入分成比例"/>
                </el-form-item>
              </el-col>
              <!-- <el-col :span="12" class="mb20">
                <el-form-item :label="'有效天数'" :prop="'revenueShares.' + index + '.validDays'" class="no-wrap-label">
                  <el-input-number v-model="item.validDays" :min="1" placeholder="请输入有效天数"/>
                </el-form-item>
              </el-col>
              <el-col :span="12" class="mb20">
                <el-form-item :label="'是否启用'" :prop="'revenueShares.' + index + '.isActive'" class="no-wrap-label">
                  <el-switch v-model="item.isActive" @change="(val) => handleActiveChange(val, index)"/>
                </el-form-item>
              </el-col>
              <el-col :span="24" class="mb20">
                <el-form-item :label="'描述'" :prop="'revenueShares.' + index + '.description'" class="no-wrap-label">
                  <el-input v-model="item.description" placeholder="请输入描述"/>
                </el-form-item>
              </el-col> -->
            </el-row>
          </div>

          <div class="add-button-container">
            <el-button type="primary" link @click="addRevenueShare">
              <el-icon><Plus /></el-icon>添加分成比例
            </el-button>
          </div>
        </div>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="visible = false">取 消</el-button>
          <el-button type="primary" @click="onSubmit" :disabled="loading">确 认</el-button>
        </span>
      </template>
    </el-dialog>
</template>

<script setup lang="ts" name="PartnersDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/rs/partners'
import { rule } from '/@/utils/validate';
import { Plus, Delete } from '@element-plus/icons-vue'
import { batchSave, delObjs, batchUpdate } from '/@/api/rs/revenueShares';
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false)
const loading = ref(false)
// 定义字典

// 提交表单数据
const form = reactive({
		partnerId:'',
	  partnerName: '',
	  partnerCode: '',
		revenueShare: 0,
	  startDate: '',
	  endDate: '',
		validDays: 0,
		validMonths: 0,
	  revenueShares: [] as any[],
});

// 定义校验规则
const dataRules = ref({
    partnerName: [{required: true, message: '合作伙伴名称不能为空', trigger: 'blur'}],
    revenueShare: [{ validator: rule.number, trigger: 'blur' }],
    validDays: [{ validator: rule.number, trigger: 'blur' }],
})

// 打开弹窗
const openDialog = (id: string) => {
    visible.value = true;
    form.partnerId = '';
    // 重置表单数据
    nextTick(() => {
        dataFormRef.value?.resetFields();
        form.revenueShares = []; // 清空分成比例数据
    });
        // 获取partners信息
    if (id) {
      form.partnerId = id;
      getPartnersData(id);
    }
};

// 初始化表单数据
const getPartnersData = (id: string) => {
  // 获取数据
  loading.value = true
  getObj({partnerId: id}).then((res: any) => {
    Object.assign(form, res.data[0])
  }).finally(() => {
    loading.value = false;
  });
};

// 添加分成比例
const addRevenueShare = () => {
  form.revenueShares.push({
    shareId: '',
    partnerId: form.partnerId,
    name: '',
    share: 0,
    description: '',
    validDays: 0,
    isActive: false,
    sortOrder: form.revenueShares.length + 1
  });
};

// 删除分成比例
const removeRevenueShare = async (shareId: string) => {
  // console.log('删除分成比例：', shareId);
  if (shareId) {
    try {
      await delObjs([shareId]);
      useMessage().success('分成比例删除成功');
    } catch (error: any) {
      useMessage().error('分成比例删除失败: ' + error.message);
      return;
    }
  }
  form.revenueShares = form.revenueShares.filter((item: any) => item.shareId !== shareId);
};

// 提交分成比例数据
const submitRevenueShares = async () => {
  try {
    if (form.revenueShares.length > 0) {
      const toUpdate = form.revenueShares.filter(item => item.shareId);
      const toAdd = form.revenueShares.filter(item => !item.shareId);

      if (toUpdate.length > 0) {
        // 如果存在需要更新的分成比例
        // console.log('更新分成比例：', toUpdate);
        await batchUpdate(toUpdate.map(item => ({ ...item, partnerId: form.partnerId })));
      }

      if (toAdd.length > 0) {
        // 如果存在需要新增的分成比例
        //console.log('新增分成比例：', toAdd);
        await batchSave(toAdd.map(item => ({ ...item, partnerId: form.partnerId })));
      }
      
      useMessage().success('分成比例保存成功');
    }
  } catch (error: any) {
    useMessage().error('分成比例保存失败: ' + error.message);
  }
};

// 修改提交方法
const onSubmit = async () => {
  const valid = await dataFormRef.value.validate().catch(() => {});
  if (!valid) return false;

  try {
    loading.value = true;
    // 先保存主表数据
    form.partnerId ? await putObj(form) : await addObj(form);
    // 再保存分成比例数据
    await submitRevenueShares();
    
    useMessage().success(form.partnerId ? '修改成功' : '添加成功');
    visible.value = false;
    emit('refresh');
  } catch (err: any) {
    useMessage().error(err.msg);
  } finally {
    loading.value = false;
  }
};

// 处理启用状态变更
const handleActiveChange = (val: boolean, currentIndex: number) => {
  if (val) {
    // 如果当前开关被打开，关闭其他所有开关
    form.revenueShares.forEach((item, index) => {
      if (index !== currentIndex) {
        item.isActive = false;
      }
    });
  }
};

// 暴露变量
defineExpose({
  openDialog
});
</script>

<style scoped>
:deep(.no-wrap-label) .el-form-item__label {
  white-space: nowrap;
}

.revenue-shares-section {
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-weight: 500;
}

.share-item {
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-bottom: 20px;
}

.share-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.share-item-header h4 {
  margin: 0;
  font-size: 15px;
  font-weight: 500;
  color: #606266;
}

.add-button-container {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.add-button-container .el-button {
  padding: 12px 20px;
}

.mb20 {
  margin-bottom: 20px;
}
</style>
