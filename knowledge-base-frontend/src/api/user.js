import request from './request'

// 用户相关接口
export function login(data) {
  // data: { username, password }
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export function getCurrentUser() {
  return request({
    url: '/user/me',
    method: 'get'
  })
}