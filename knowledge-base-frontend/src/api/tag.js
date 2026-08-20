import request from './request'

// 标签相关接口
export function createTag(data) {
  // { name }
  return request({
    url: '/tag/create',
    method: 'post',
    data
  })
}

export function listTags() {
  return request({
    url: '/tag/list',
    method: 'get'
  })
}

export function updateTag(data) {
  // { id, name }
  return request({
    url: '/tag/update',
    method: 'put',
    data
  })
}

export function deleteTag(id) {
  return request({
    url: `/tag/delete/${id}`,
    method: 'delete'
  })
}