import axios from 'axios'
import { ElMessage } from 'element-plus'

// 1.创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 5000 // 请求超时时间 5秒
})

// 2. 请求拦截器
request.interceptors.request.use(config => {
  //（1）从localStorage获取用户信息
  const userInfoStr = localStorage.getItem('user_info')
  if (userInfoStr) {
    const userInfo = JSON.parse(userInfoStr)
    //（2）把userId塞到请求头里
    config.headers['userId'] = userInfo.userId
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 3. 响应拦截器
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