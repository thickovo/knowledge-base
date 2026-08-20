<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="hover">
      <div class="auth-header">
        <h1 class="auth-title">创建新账号</h1>
        <p class="auth-subtitle">注册以开始管理你的知识库</p>
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
            placeholder="请输入密码(不少于 6 位)"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirm">
          <el-input
            v-model="form.confirm"
            type="password"
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="onSubmit"
          >
            注册
          </el-button>
        </el-form-item>
        <div class="auth-footer">
          已有账号?
          <router-link to="/login" class="link">返回登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { register } from '@/api/user'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirm: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不少于 6 位', trigger: 'blur' }
  ],
  confirm: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_rule, value, cb) => {
        if (value !== form.password) cb(new Error('两次输入的密码不一致'))
        else cb()
      },
      trigger: 'blur'
    }
  ]
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
    await register({ username: form.username, password: form.password })
    ElMessage.success('注册成功,请登录')
    router.push('/login')
  } catch {
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