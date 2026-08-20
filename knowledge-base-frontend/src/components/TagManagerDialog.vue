<template>
  <el-dialog
    v-model="visible"
    title="标签管理"
    width="520px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="tag-manager">
      <div class="tag-create">
        <el-input
          v-model="newTagName"
          placeholder="输入新标签名称"
          clearable
          :maxlength="20"
          @keyup.enter="handleCreate"
        >
          <template #append>
            <el-button type="primary" :loading="creating" @click="handleCreate">
              新建
            </el-button>
          </template>
        </el-input>
      </div>

      <el-divider />

      <div v-loading="loading" class="tag-list">
        <el-empty v-if="!loading && tags.length === 0" description="暂无标签" />
        <div v-else class="tag-grid">
          <el-tag
            v-for="tag in tags"
            :key="tag.id"
            closable
            class="tag-item"
            @close="handleDelete(tag)"
          >
            {{ tag.name }}
          </el-tag>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listTags, createTag, deleteTag } from '@/api/tag'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})
const emit = defineEmits(['update:modelValue', 'changed'])

const visible = ref(props.modelValue)
watch(() => props.modelValue, (v) => (visible.value = v))
watch(visible, (v) => emit('update:modelValue', v))

const tags = ref([])
const loading = ref(false)
const creating = ref(false)
const newTagName = ref('')

async function loadTags() {
  loading.value = true
  try {
    tags.value = await listTags()
  } catch {
    // 拦截器已提示
  } finally {
    loading.value = false
  }
}

async function handleCreate() {
  const name = newTagName.value.trim()
  if (!name) {
    ElMessage.warning('请输入标签名称')
    return
  }
  creating.value = true
  try {
    await createTag({ name })
    ElMessage.success('创建成功')
    newTagName.value = ''
    await loadTags()
    emit('changed')
  } catch {
    // 拦截器已提示
  } finally {
    creating.value = false
  }
}

async function handleDelete(tag) {
  try {
    await ElMessageBox.confirm(
      `确认删除标签「${tag.name}」吗?关联该标签的文档关联关系也会被删除。`,
      '删除确认',
      { type: 'warning' }
    )
  } catch {
    return
  }
  try {
    await deleteTag(tag.id)
    ElMessage.success('删除成功')
    await loadTags()
    emit('changed')
  } catch {
    // 拦截器已提示
  }
}

function handleClose() {
  newTagName.value = ''
}

watch(visible, (v) => {
  if (v) loadTags()
})
</script>

<style scoped>
.tag-manager {
  min-height: 200px;
}

.tag-create {
  display: flex;
  gap: 8px;
}

.tag-list {
  max-height: 360px;
  overflow-y: auto;
}

.tag-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  font-size: 13px;
  padding: 6px 10px;
}
</style>