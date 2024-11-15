<template>
  <div class="right-panel">
    <div class="panel-header">
      <h3 class="panel-title">工单记录</h3>
      <el-button type="primary" size="small" @click="handleAddNote">
        增加说明
      </el-button>
    </div>
    <div v-if="displayLogs.length === 0" class="empty-text">
      {{ emptyText }}
    </div>
    <template v-else>
      <div class="timeline">
        <div class="timeline-item" v-for="(item, index) in displayLogs" :key="index">
          <!-- {{ console.log('工单记录item:', item) }} -->
          <div class="timeline-avatar" :class="{ 'breathing': index === 0 }">
            <el-avatar 
              :size="24"
              :src="item.performedByAvatar ? proxy.baseURL + item.performedByAvatar : defaultAvatar" 
              :username="item.performedByName"
            />
          </div>
          <div class="timeline-content">
            <div class="log-title">{{ item.title }}</div>
            <div class="log-time">{{ item.time }}</div>
          </div>
        </div>
      </div>
      <div class="view-more" v-if="logs.length > limit">
        <el-button link type="primary" @click="$emit('view-more')">
          查看更多
          <el-icon class="el-icon--right"><ArrowRight /></el-icon>
        </el-button>
      </div>
    </template>
  </div>

  <el-dialog
    v-model="dialogVisible"
    title="增说明"
    width="30%"
  >
    <el-input
      v-model="noteContent"
      type="textarea"
      rows="4"
      placeholder="请输入说明内容"
    />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNote">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { addObj } from '/@/api/rs/workLog';
import { ElMessage } from 'element-plus';
import defaultAvatar from '/@/assets/images/defaultAvatar.png'; // 导入默认头像
const { proxy } = getCurrentInstance();

const props = defineProps({
  logs: {
    type: Array,
    default: () => []
  },
  limit: {
    type: Number,
    default: Infinity
  },
  workId: {
    type: String,
    default: ''
  },
  currentUser: {
    type: Object,
    default: () => ({})
  },
  emptyText: {
    type: String,
    default: '暂无数据'
  }
});

const emit = defineEmits(['view-more', 'add-note', 'refresh']);

const displayLogs = computed(() => {
  return props.logs.slice(0, props.limit);
});

const dialogVisible = ref(false);
const noteContent = ref('');

const handleAddNote = () => {
  dialogVisible.value = true;
};

const submitNote = async () => {
  if (!noteContent.value.trim()) {
    ElMessage.warning('说明内容不能为空');
    return;
  }

  try {
    await addObj({
      workId: props.workId,
      operation: 'ADD_COMMENT',
      performedBy: props.currentUser.userId,
      details: noteContent.value.trim()
    });

    ElMessage.success('添加说明成功');
    dialogVisible.value = false;
    noteContent.value = '';
    emit('refresh');
  } catch (error) {
    console.error('添加说明失败:', error);
    ElMessage.error('添加说明失败');
  }
};
</script>

<style scoped>
.right-panel {
	height: 100%;
	position: relative;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.panel-title {
  margin-bottom: 0;
  border-bottom: none;
}

.timeline {
  position: relative;
  padding-left: 60px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 20px;
  top: 0;
  bottom: 0;
  width: 2px;
  background-color: #e4e7ed;
}

.timeline-item {
  position: relative;
  margin-bottom: 20px;
  padding: 5px 0;
}

.timeline-avatar {
  position: absolute;
  left: -55px;
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
  box-shadow: 0 0 0 2px #409eff;
  overflow: hidden;
  z-index: 1;
}

.timeline-avatar.breathing {
  box-shadow: 0 0 0 2px #ff7875;
  animation: breathing 2s ease-out infinite;
}

@keyframes breathing {
  0% {
    box-shadow: 0 0 0 2px #ff7875, 0 0 0 2px rgba(255, 120, 117, 0.4);
  }
  100% {
    box-shadow: 0 0 0 2px #ff7875, 0 0 0 15px rgba(255, 120, 117, 0);
  }
}

.timeline-avatar:not(.breathing):hover {
  transform: translateY(-50%) scale(1.1);
  transition: transform 0.3s ease;
  box-shadow: 0 0 0 2px #409eff, 0 0 10px rgba(64, 158, 255, 0.8);
}

.timeline-avatar.breathing:hover {
  transform: translateY(-50%) scale(1.1);
  transition: transform 0.3s ease;
  box-shadow: 0 0 0 2px #ff7875, 0 0 10px rgba(255, 120, 117, 0.8);
}

.timeline-content {
  background-color: #f5f7fa;
  padding: 10px 15px;
  border-radius: 4px;
}

.log-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
  line-height: 1.4;
}

.log-time {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.view-more {
  text-align: center;
  padding: 10px 0;
  margin-top: 10px;
  border-top: 1px solid #ebeef5;
}

/* 悬停效果 */
.view-more .el-button:hover {
  text-decoration: underline;
}

.empty-text {
  text-align: center;
  color: #909399;
  padding: 20px 0;
  font-size: 14px;
  margin-top: 20px;
}
</style>

