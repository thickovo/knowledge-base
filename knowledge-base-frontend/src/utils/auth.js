// 与鉴权相关的工具方法:统一读写 localStorage 中的 token

const TOKEN_KEY = 'kb_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}