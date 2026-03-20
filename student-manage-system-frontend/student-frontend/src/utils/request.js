import axios from 'axios'
import { ElMessage } from 'element-plus'
import { removeUser } from './auth'

const request = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const userInfoStr = localStorage.getItem('user_info')
  if (userInfoStr) {
    const userInfo = JSON.parse(userInfoStr)
    config.headers['userId'] = userInfo.userId
  }
  return config
}, error => {
  console.error('请求错误:', error)
  return Promise.reject(error)
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    
    if (res.code === 401) {
      ElMessage.error('登录已过期，请重新登录')
      removeUser()
      window.location.href = '/login'
      return Promise.reject(new Error(res.msg || '未授权'))
    }
    
    if (res.code === 403) {
      ElMessage.error('权限不足')
      return Promise.reject(new Error(res.msg || '权限不足'))
    }
    
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  error => {
    console.error('响应错误:', error)
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          removeUser()
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误，请稍后再试')
          break
        default:
          ElMessage.error(error.message || '请求失败')
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请检查网络')
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default request
