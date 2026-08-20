<template>
  <div class="detail-page">
    <header class="detail-header">
      <el-button text @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回列表</span>
      </el-button>
      <div class="actions">
        <el-button type="primary" @click="goEdit">
          <el-icon><Edit /></el-icon>
          <span>编辑</span>
        </el-button>
        <el-button type="danger" @click="onDelete">
          <el-icon><Delete /></el-icon>
          <span>删除</span>
        </el-button>
      </div>
    </header>

    <div v-loading="loading" class="detail-body">
      <template v-if="document">
        <h1 class="detail-title">{{ document.title }}</h1>
        <div class="detail-meta">
          <span>
            <el-icon><Clock /></el-icon>
            创建: {{ formatTime(document.createTime) }}
          </span>
          <span v-if="document.updateTime">
            <el-icon><Refresh /></el-icon>
            更新: {{ formatTime(document.updateTime) }}
          </span>
        </div>
        <el-divider />
        <div class="detail-content">{{ document.content || '(无内容)' }}</div>
      </template>
      <el-empty v-else-if="!loading" description="文档不存在或已被删除" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Edit,
  Delete,
  Clock,
  Refresh
} from '@element-plus/icons-vue'
import { getDocument, deleteDocument } from '@/api/document'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const document = ref(null)

async function load() {
  loading.value = true
  try {
    document.value = await getDocument(route.params.id)
  } catch {
    document.value = null
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/documents')
}

function goEdit() {
  router.push(`/document/edit/${route.params.id}`)
}

async function onDelete() {
  try {
    await ElMessageBox.confirm(
      '确认删除该文档吗?此操作不可恢复。',
      '删除确认',
      { type: 'warning' }
    )
  } catch {
    return
  }
  try {
    await deleteDocument(route.params.id)
    ElMessage.success('删除成功')
    router.push('/documents')
  } catch {}
}

function formatTime(t) {
  if (!t) return ''
  let date
  if (Array.isArray(t)) {
    date = new Date(t[0], (t[1] || 1) - 1, t[2] || 1, t[3] || 0, t[4] || 0, t[5] || 0)
  } else {
    date = new Date(t)
  }
  if (isNaN(date.getTime())) return ''
  const pad = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

onMounted(load)
</script>

<style scoped>
.detail-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f7fa;
}

.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
}

.actions {
  display: flex;
  gap: 8px;
}

.detail-body {
  flex: 1;
  overflow-y: auto;
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
  padding: 32px 24px;
  background-color: #fff;
  border-radius: 8px;
  margin-top: 20px;
  margin-bottom: 20px;
}

.detail-title {
  margin: 0;
  font-size: 26px;
  color: #303133;
}

.detail-meta {
  display: flex;
  gap: 24px;
  margin-top: 12px;
  font-size: 13px;
  color: #909399;
}

.detail-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-content {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>