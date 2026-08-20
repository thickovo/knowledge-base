import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

// 后端统一返回结构: { code, message, data }
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截:自动带上 Bearer Token
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截:统一处理 code != 200 的情况
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && typeof res.code !== 'undefined') {
      if (res.code === 200) {
        return res.data
      }
      // 401 视为未登录,清理 token 跳登录页
      if (res.code === 401) {
        ElMessage.error(res.message || '登录已过期,请重新登录')
        removeToken()
        router.push('/login')
        return Promise.reject(new Error(res.message || '未登录'))
      }
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return response.data
  },
  (error) => {
    // HTTP 错误处理
    if (error.response && error.response.status === 401) {
      removeToken()
      router.push('/login')
      ElMessage.error('登录已过期,请重新登录')
    } else {
      ElMessage.error(error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default request