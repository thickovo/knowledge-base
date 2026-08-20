import request from './request'

// 文档相关接口
export function createDocument(data) {
  // { title, content, parentId, tagIds }
  return request({
    url: '/document/create',
    method: 'post',
    data
  })
}

export function listDocuments(params) {
  // params: { parentId, tagId, keyword }
  return request({
    url: '/document/list',
    method: 'get',
    params
  })
}

export function getDocument(id) {
  return request({
    url: `/document/${id}`,
    method: 'get'
  })
}

export function updateDocument(data) {
  // { id, title, content }
  return request({
    url: '/document/update',
    method: 'put',
    data
  })
}

export function deleteDocument(id) {
  return request({
    url: `/document/delete/${id}`,
    method: 'delete'
  })
}