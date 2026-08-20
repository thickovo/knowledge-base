<template>
  <div class="doc-page">
    <!-- 顶部 Header -->
    <header class="doc-header">
      <div class="brand">
        <el-icon :size="22" color="#fff"><Document /></el-icon>
        <span class="brand-text">知识库管理系统</span>
      </div>
      <div class="header-actions">
        <el-dropdown @command="handleUserCommand">
          <span class="user-info">
            <el-icon><User /></el-icon>
            <span class="username">{{ username || '未登录' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- 顶部筛选条 -->
    <div class="doc-toolbar">
      <el-input
        v-model="filters.keyword"
        placeholder="搜索文档标题或内容"
        clearable
        :prefix-icon="Search"
        class="toolbar-search"
        @input="onSearchInput"
      />
      <el-select
        v-model="filters.tagId"
        placeholder="按标签筛选"
        clearable
        class="toolbar-tag"
        @change="reloadList"
      >
        <el-option
          v-for="tag in tags"
          :key="tag.id"
          :label="tag.name"
          :value="tag.id"
        />
      </el-select>
      <el-button @click="tagDialogVisible = true">
        <el-icon><CollectionTag /></el-icon>
        <span>标签管理</span>
      </el-button>
      <el-button type="primary" @click="goCreate">
        <el-icon><Plus /></el-icon>
        <span>新建文档</span>
      </el-button>
    </div>

    <!-- 主体区域:左侧树 + 右侧卡片 -->
    <div class="doc-body">
      <aside class="doc-tree">
        <div class="tree-header">
          <span>目录</span>
          <el-button text type="primary" @click="goToRoot">根目录</el-button>
        </div>
        <el-tree
          ref="treeRef"
          v-loading="treeLoading"
          :data="treeData"
          :props="treeProps"
          node-key="id"
          lazy
          :load="loadTreeNode"
          :highlight-current="true"
          :expand-on-click-node="false"
          empty-text="暂无内容"
          @node-click="onTreeSelect"
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <el-icon :size="14">
                <Folder v-if="data.isLeaf === false || isFolder(data)" />
                <Document v-else />
              </el-icon>
              <span class="tree-label">{{ data.label }}</span>
            </div>
          </template>
        </el-tree>
      </aside>

      <main class="doc-content">
        <div class="content-header">
          <span class="current-path">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item @click="goToRoot">根目录</el-breadcrumb-item>
              <el-breadcrumb-item v-for="(p, idx) in breadcrumb" :key="idx">
                {{ p }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </span>
          <span class="result-count">共 {{ filteredDocs.length }} 条</span>
        </div>

        <div v-loading="listLoading" class="doc-grid">
          <el-empty
            v-if="!listLoading && filteredDocs.length === 0"
            description="该目录下暂无文档"
          />
          <el-card
            v-for="doc in filteredDocs"
            :key="doc.id"
            class="doc-card"
            shadow="hover"
            @click="goDetail(doc)"
          >
            <div class="doc-card-title">
              <el-icon><Document /></el-icon>
              <span>{{ doc.title }}</span>
            </div>
            <div class="doc-card-meta">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(doc.createTime) }}</span>
            </div>
            <div v-if="doc.content" class="doc-card-preview">
              {{ previewContent(doc.content) }}
            </div>
          </el-card>
        </div>
      </main>
    </div>

    <TagManagerDialog
      v-model="tagDialogVisible"
      @changed="loadTags"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Document,
  Folder,
  User,
  ArrowDown,
  Search,
  CollectionTag,
  Plus,
  Clock
} from '@element-plus/icons-vue'
import { listDocuments } from '@/api/document'
import { listTags } from '@/api/tag'
import { getCurrentUser } from '@/api/user'
import { removeToken } from '@/utils/auth'
import TagManagerDialog from '@/components/TagManagerDialog.vue'

const router = useRouter()

// 当前用户
const username = ref('')
// 标签列表
const tags = ref([])

// 过滤条件
const filters = reactive({
  keyword: '',
  tagId: null
})

// 当前选中的目录
const currentParentId = ref(0)
const breadcrumb = ref([])

// 树
const treeRef = ref(null)
const treeData = ref([])
const treeLoading = ref(false)
const treeProps = { label: 'label', children: 'children', isLeaf: 'isLeaf' }

// 列表
const listLoading = ref(false)
const docs = ref([])

// 标签管理弹窗
const tagDialogVisible = ref(false)

// 防抖搜索
let searchTimer = null
function onSearchInput() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(reloadList, 300)
}

// 加载标签
async function loadTags() {
  try {
    tags.value = await listTags()
  } catch {}
}

// 加载当前用户信息
async function loadUser() {
  try {
    const info = await getCurrentUser()
    username.value = info.username
  } catch {
    username.value = ''
  }
}

// 判断节点是否是文件夹(有子节点的 doc 也视为文件夹)
// 这里统一把每个文档都当作潜在父节点,后端会返回它的子文档
function isFolder(_data) {
  return false // 实际节点 leaf 状态由懒加载结果决定
}

// 懒加载树节点:加载某 parentId 下的子文档
async function loadTreeNode(node, resolve) {
  const parentId = node.level === 0 ? 0 : node.data.id
  try {
    const list = await listDocuments({ parentId, tagId: null, keyword: '' })
    const children = list.map((d) => ({
      id: d.id,
      label: d.title,
      isLeaf: false // 都允许展开,后端会决定是否真有子节点
    }))
    resolve(children)
    // 如果一个文档展开后没有子节点,Element Plus 会显示为空,我们标记为叶子节点
    if (children.length === 0 && node.level > 0) {
      node.data.isLeaf = true
      node.isLeaf = true
    }
  } catch (e) {
    resolve([])
  }
}

// 刷新树:重新加载根节点
function reloadTree() {
  treeData.value = []
  treeLoading.value = true
  // 触发根节点懒加载
  setTimeout(() => {
    treeLoading.value = false
    // 直接通过 nextTick 强制刷新树
    if (treeRef.value) {
      treeRef.value.store.nodesMap = {}
    }
  }, 0)
}

// 文档列表懒加载
async function loadList() {
  listLoading.value = true
  try {
    docs.value = await listDocuments({
      parentId: currentParentId.value,
      tagId: filters.tagId || undefined,
      keyword: filters.keyword || undefined
    })
  } catch {
    docs.value = []
  } finally {
    listLoading.value = false
  }
}

async function reloadList() {
  await loadList()
}

// 树节点点击:切换目录
function onTreeSelect(data) {
  currentParentId.value = data.id
  breadcrumb.value = [data.label]
  loadList()
}

function goToRoot() {
  currentParentId.value = 0
  breadcrumb.value = []
  loadList()
}

// 是否将搜索视为对所有文档,而非限定目录
const filteredDocs = computed(() => docs.value)

// 卡片预览:取前 80 字
function previewContent(text) {
  if (!text) return ''
  const cleaned = text.replace(/\s+/g, ' ').trim()
  return cleaned.length > 80 ? cleaned.slice(0, 80) + '...' : cleaned
}

// 时间格式化
function formatTime(t) {
  if (!t) return ''
  // 后端传回 LocalDateTime,axios 默认会转成数组 [yyyy, MM, dd, HH, mm, ss, ...]
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

// 路由跳转
function goCreate() {
  router.push({
    path: '/document/create',
    query: { parentId: currentParentId.value }
  })
}

function goDetail(doc) {
  router.push(`/document/${doc.id}`)
}

// 用户菜单
function handleUserCommand(cmd) {
  if (cmd === 'logout') {
    removeToken()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}

onMounted(async () => {
  await Promise.all([loadUser(), loadTags()])
  await loadList()
})
</script>

<style scoped>
.doc-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f7fa;
}

.doc-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 56px;
  background: linear-gradient(90deg, #409eff 0%, #5e72e4 100%);
  color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.15);
}

.username {
  font-size: 14px;
}

.doc-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
}

.toolbar-search {
  width: 280px;
}

.toolbar-tag {
  width: 180px;
}

.doc-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.doc-tree {
  width: 260px;
  background-color: #fff;
  border-right: 1px solid #ebeef5;
  padding: 12px;
  overflow-y: auto;
}

.tree-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  font-weight: 600;
  color: #303133;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 6px;
  width: 100%;
}

.tree-label {
  font-size: 13px;
}

.doc-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px 20px;
  overflow: hidden;
}

.content-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
  color: #606266;
}

.doc-grid {
  flex: 1;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
  align-content: start;
}

.doc-card {
  cursor: pointer;
  transition: transform 0.15s;
}

.doc-card:hover {
  transform: translateY(-2px);
}

.doc-card-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  font-size: 15px;
  color: #303133;
  margin-bottom: 8px;
  word-break: break-all;
}

.doc-card-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.doc-card-preview {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  max-height: 60px;
  overflow: hidden;
}
</style>