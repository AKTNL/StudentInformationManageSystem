import axios from 'axios'
import { ElMessage } from 'element-plus'

// 1.创建axios实例
const request = axios.create({
    baseURL: 'http://localhost:8081',
    timeout: 5000 // 请求超时时间 5秒
})

// 2. 请求拦截器 (可以在这里统一加 Token，以后进阶会用到)
request.interceptors.request.use(config => {
  return config
}, error => {
  return Promise.reject(error)
})

// 3. 响应拦截器 (可以在这里统一处理错误)
request.interceptors.response.use(
  response => {
    // response.data 是后端返回的 Result 对象 {code: 200, ...}
    return response
  },
  error => {
    ElMessage.error('请求失败，请检查后端服务')
    return Promise.reject(error)
  }
)

export default request