<template>
  <div class="edit-page">
    <header class="edit-header">
      <el-button text @click="onCancel">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回</span>
      </el-button>
      <h2 class="edit-title">{{ isEdit ? '编辑文档' : '新建文档' }}</h2>
      <div class="edit-actions">
        <el-button @click="onCancel">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">
          保存
        </el-button>
      </div>
    </header>

    <div v-loading="loading" class="edit-body">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="onSave"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入文档标题"
            :maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="父目录" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="parentOptions"
            :props="parentSelectProps"
            node-key="id"
            placeholder="选择父目录(可选)"
            clearable
            check-strictly
            :render-after-expand="false"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="标签" prop="tagIds">
          <el-select
            v-model="form.tagIds"
            multiple
            collapse-tags
            collapse-tags-tooltip
            placeholder="选择标签(可多选)"
            style="width: 100%"
          >
            <el-option
              v-for="tag in tags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            placeholder="请输入文档内容"
            :autosize="{ minRows: 10, maxRows: 24 }"
            :maxlength="10000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import {
  getDocument,
  createDocument,
  updateDocument,
  listDocuments
} from '@/api/document'
import { listTags } from '@/api/tag'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const formRef = ref(null)
const loading = ref(false)
const saving = ref(false)

const form = reactive({
  title: '',
  content: '',
  parentId: 0,
  tagIds: []
})

const rules = {
  title: [{ required: true, message: '请输入文档标题', trigger: 'blur' }]
}

const tags = ref([])
const parentOptions = ref([{ id: 0, label: '根目录', children: [] }])
const parentSelectProps = { label: 'label', children: 'children', value: 'id' }

// 把后端扁平文档列表转成树形,供 el-tree-select 使用
function buildTree(list) {
  const map = new Map()
  const roots = []
  list.forEach((d) => {
    map.set(d.id, { id: d.id, label: d.title, children: [] })
  })
  list.forEach((d) => {
    const node = map.get(d.id)
    const pid = d.parentId || 0
    if (pid === 0) {
      roots.push(node)
    } else {
      const parent = map.get(pid)
      if (parent) parent.children.push(node)
      else roots.push(node)
    }
  })
  return roots
}

async function loadParents() {
  try {
    // 一次性拉根目录的所有文档构建简单树;深层文档只能在下拉中展开时显示
    // 这里我们调用 listDocuments(parentId=0) 拿到顶层,然后逐层展开
    // 为简化,只展示当前可见层级
    const rootDocs = await listDocuments({ parentId: 0, keyword: '', tagId: null })
    // 递归获取所有子节点
    const allDocs = [...rootDocs]
    async function fetchChildren(docs) {
      for (const d of docs) {
        const children = await listDocuments({ parentId: d.id, keyword: '', tagId: null })
        if (children.length) {
          allDocs.push(...children)
          await fetchChildren(children)
        }
      }
    }
    await fetchChildren(rootDocs)
    const tree = buildTree(allDocs)
    parentOptions.value = [{ id: 0, label: '根目录', children: tree }]
  } catch {
    parentOptions.value = [{ id: 0, label: '根目录', children: [] }]
  }
}

async function loadTags() {
  try {
    tags.value = await listTags()
  } catch {
    tags.value = []
  }
}

async function loadDocument() {
  if (!isEdit.value) return
  loading.value = true
  try {
    const doc = await getDocument(route.params.id)
    form.title = doc.title
    form.content = doc.content || ''
    form.parentId = doc.parentId || 0
    // tagIds 编辑接口未返回,默认空
    form.tagIds = []
  } catch {
    ElMessage.error('加载文档失败')
  } finally {
    loading.value = false
  }
}

async function onSave() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await updateDocument({
        id: Number(route.params.id),
        title: form.title,
        content: form.content
      })
      ElMessage.success('保存成功')
    } else {
      await createDocument({
        title: form.title,
        content: form.content,
        parentId: form.parentId || 0,
        tagIds: form.tagIds
      })
      ElMessage.success('创建成功')
    }
    router.push('/documents')
  } catch {
    // 拦截器已提示
  } finally {
    saving.value = false
  }
}

function onCancel() {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/documents')
  }
}

onMounted(async () => {
  await Promise.all([loadTags(), loadParents()])
  await loadDocument()
})
</script>

<style scoped>
.edit-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f7fa;
}

.edit-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 20px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
}

.edit-title {
  margin: 0;
  font-size: 18px;
  flex: 1;
}

.edit-actions {
  display: flex;
  gap: 8px;
}

.edit-body {
  flex: 1;
  overflow-y: auto;
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  margin-top: 20px;
  margin-bottom: 20px;
}
</style>