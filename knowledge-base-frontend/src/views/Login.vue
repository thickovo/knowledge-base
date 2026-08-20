<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="hover">
      <div class="auth-header">
        <h1 class="auth-title">知识库管理系统</h1>
        <p class="auth-subtitle">登录以继续</p>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="onSubmit"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            :loading="loading"
            style="width: 100%"
            @click="onSubmit"
          >
            登录
          </el-button>
        </el-form-item>
        <div class="auth-footer">
          还没有账号?
          <router-link to="/register" class="link">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/user'
import { setToken } from '@/utils/auth'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function onSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    const token = await login(form)
    setToken(token)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/documents'
    router.push(redirect)
  } catch (e) {
    // 拦截器已统一提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.auth-card {
  width: 380px;
  border-radius: 8px;
}

.auth-header {
  text-align: center;
  margin-bottom: 24px;
}

.auth-title {
  margin: 0;
  font-size: 22px;
  color: #303133;
}

.auth-subtitle {
  margin: 8px 0 0;
  color: #909399;
  font-size: 13px;
}

.auth-footer {
  text-align: center;
  font-size: 13px;
  color: #909399;
}

.link {
  color: #409eff;
  margin-left: 4px;
}

.link:hover {
  text-decoration: underline;
}
</style>