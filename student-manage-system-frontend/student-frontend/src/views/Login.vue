<script setup>
import {reactive, ref, onMounted} from 'vue'
import { ElMessage } from 'element-plus';
import { useRouter, useRoute } from 'vue-router';
import { getCaptchaAPI,loginAPI,registerAPI, sendResetCodeAPI, resetPasswordAPI } from '../api/user';
import { setUser, isLoggedIn } from '../utils/auth';

const router = useRouter()
const route = useRoute()

const isRegister = ref(false)
const showForgotPassword = ref(false)
const countdown = ref(0)

const loginForm = reactive({
  username: '',
  password: '',
  captchaCode: '',
  captchaKey: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  captchaCode: '',
  captchaKey: '',
  studentNo: '',
  realName: ''
})

const forgotForm = reactive({
  username: '',
  captchaCode: '',
  captchaKey: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

const captchaUrl = ref('')

const loadCaptcha = () => {
  getCaptchaAPI()
    .then(res => {
      if(res.code === 200) {
        captchaUrl.value = res.data.image
        loginForm.captchaKey = res.data.key
        registerForm.captchaKey = res.data.key
        forgotForm.captchaKey = res.data.key
      }
    })
    .catch(err => {
      console.error('获取验证码失败:', err)
      ElMessage.error('获取验证码失败，请检查后端服务')
    })
}

onMounted(() => {
  if (isLoggedIn()) {
    router.push('/home')
    return
  }
  loadCaptcha()
})

const handleLogin = () =>{
  if(!loginForm.username || !loginForm.password || !loginForm.captchaCode){
    ElMessage.warning('请输入完整信息')
    return
  }

  loginAPI(loginForm)
    .then(res =>{
      console.log('后端返回的数据：', res)
      ElMessage.success('登录成功！欢迎你，'+res.data.nickname)

      setUser(res.data)

      const redirect = route.query.redirect || '/home'
      router.push(redirect)
    }).catch(err=>{
      console.error(err)
      loadCaptcha()
    })

}

const handleRegister = () => {
  if(!registerForm.username || !registerForm.password || 
      !registerForm.confirmPassword || !registerForm.captchaCode ||
      !registerForm.studentNo || !registerForm.realName){
    ElMessage.warning('请填写完整注册信息')
    return
  }
  if(registerForm.password !== registerForm.confirmPassword){
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  registerAPI({
    username: registerForm.username,
    password: registerForm.password,
    confirmPassword: registerForm.confirmPassword,
    captchaCode: registerForm.captchaCode,
    captchaKey: registerForm.captchaKey,
    studentNo: registerForm.studentNo,
    realName: registerForm.realName
  })
  .then(res =>{
    ElMessage.success('注册成功！请登录')
    isRegister.value = false
    registerForm.username = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''
    registerForm.captchaCode = ''
    registerForm.studentNo = ''
    registerForm.realName = ''
    loadCaptcha()
  })
  .catch(err => {
    console.error(err)
    loadCaptcha()
  })
}

const toggleMode = () => {
  isRegister.value = !isRegister.value
  loadCaptcha()
  loginForm.captchaCode = ''
  registerForm.captchaCode = ''
}

const openForgotPassword = () => {
  showForgotPassword.value = true
  loadCaptcha()
  forgotForm.username = ''
  forgotForm.captchaCode = ''
  forgotForm.code = ''
  forgotForm.newPassword = ''
  forgotForm.confirmPassword = ''
}

const handleSendCode = () => {
  if(!forgotForm.username || !forgotForm.captchaCode){
    ElMessage.warning('请输入用户名和验证码')
    return
  }

  sendResetCodeAPI({
    username: forgotForm.username,
    captchaKey: forgotForm.captchaKey,
    captchaCode: forgotForm.captchaCode
  })
  .then(res => {
    ElMessage.success(res.msg)
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if(countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
    loadCaptcha()
  })
  .catch(err => {
    console.error(err)
    loadCaptcha()
  })
}

const handleResetPassword = () => {
  if(!forgotForm.username || !forgotForm.code || !forgotForm.newPassword || !forgotForm.confirmPassword){
    ElMessage.warning('请填写完整信息')
    return
  }
  if(forgotForm.newPassword !== forgotForm.confirmPassword){
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  if(forgotForm.newPassword.length < 6){
    ElMessage.warning('密码长度不能少于6位')
    return
  }

  resetPasswordAPI({
    username: forgotForm.username,
    code: forgotForm.code,
    newPassword: forgotForm.newPassword,
    confirmPassword: forgotForm.confirmPassword
  })
  .then(res => {
    ElMessage.success('密码重置成功！请登录')
    showForgotPassword.value = false
  })
  .catch(err => {
    console.error(err)
  })
}

const hasLogo = ref(true)
</script>

<template>
  <div class="login-container">
    <div class="floating-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
      <div class="shape shape-5"></div>
      <div class="shape shape-6"></div>
    </div>

    <el-card class="login-card animate-slide-in">
      <template #header>
        <div class="card-header">
          <div class="logo-wrapper">
            <img src="../assets/logo.png" alt="logo" class="logo-img animate-float" v-if="hasLogo"></img>
          </div>
          <h2 class="title">{{ isRegister ? '新用户注册' : '学生管理系统' }}</h2>
          <p class="subtitle">{{ isRegister ? '加入我们，开启学习之旅' : '欢迎回来，请登录您的账号' }}</p>
        </div>
      </template>

      <el-form v-if="!isRegister" label-width="80px" @submit.prevent="handleLogin" class="login-form">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>

        <el-form-item label="验证码">
            <div class="captcha-wrapper">
                <el-input v-model="loginForm.captchaCode" placeholder="输入验证码" prefix-icon="Key" class="captcha-input"/>
                <img 
                    :src="captchaUrl" 
                    @click="loadCaptcha" 
                    class="captcha-img"
                    title="点击刷新"
                />
            </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" native-type="submit" class="login-btn">
            <el-icon class="btn-icon"><Unlock /></el-icon>
            登录
          </el-button>
        </el-form-item>

        <div class="switch-links">
          <el-button link type="primary" @click="toggleMode" class="link-btn">
            <el-icon><UserFilled /></el-icon>
            没有账号？去注册
          </el-button>
          <el-button link type="warning" @click="openForgotPassword" class="link-btn">
            <el-icon><QuestionFilled /></el-icon>
            忘记密码？
          </el-button>
        </div>
      </el-form>

      <el-form v-else label-width="80px" class="login-form">
        <el-form-item label="用户名">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="User"/>
        </el-form-item>

        <el-form-item label="学号">
          <el-input v-model="registerForm.studentNo" placeholder="请输入学号（必填）" prefix-icon="Postcard"/>
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名（必填）" prefix-icon="Avatar"/>
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" prefix-icon="Lock" show-password/>
        </el-form-item>

        <el-form-item label="验证码">
            <div class="captcha-wrapper">
                <el-input v-model="registerForm.captchaCode" placeholder="输入验证码" prefix-icon="Key" class="captcha-input"/>
                <img 
                    :src="captchaUrl" 
                    @click="loadCaptcha" 
                    class="captcha-img"
                    title="点击刷新"
                />
            </div>
        </el-form-item>

        <el-form-item>
          <el-button type="success" @click="handleRegister" class="login-btn">
            <el-icon class="btn-icon"><CircleCheck /></el-icon>
            立即注册
          </el-button>
        </el-form-item>

        <div class="switch-links">
          <el-button link type="primary" @click="toggleMode" class="link-btn">
            <el-icon><Back /></el-icon>
            已有账号？去登录
          </el-button>
        </div>
      </el-form>
    </el-card>

    <el-dialog v-model="showForgotPassword" title="找回密码" width="450px" class="forgot-dialog">
      <el-form label-width="100px" class="forgot-form">
        <el-form-item label="用户名">
          <el-input v-model="forgotForm.username" placeholder="请输入用户名" prefix-icon="User"/>
        </el-form-item>
        
        <el-form-item label="图形验证码">
          <div class="captcha-wrapper">
            <el-input v-model="forgotForm.captchaCode" placeholder="输入验证码" prefix-icon="Key" class="captcha-input"/>
            <img 
              :src="captchaUrl" 
              @click="loadCaptcha" 
              class="captcha-img"
              title="点击刷新"
            />
          </div>
        </el-form-item>

        <el-form-item label="获取验证码">
          <el-button 
            type="primary" 
            @click="handleSendCode" 
            :disabled="countdown > 0"
            class="send-code-btn"
          >
            {{ countdown > 0 ? countdown + '秒后重试' : '发送验证码' }}
          </el-button>
        </el-form-item>

        <el-form-item label="验证码">
          <el-input v-model="forgotForm.code" placeholder="请输入收到的验证码" prefix-icon="Message"/>
        </el-form-item>

        <el-form-item label="新密码">
          <el-input v-model="forgotForm.newPassword" type="password" placeholder="请输入新密码（至少6位）" prefix-icon="Lock" show-password/>
        </el-form-item>

        <el-form-item label="确认新密码">
          <el-input v-model="forgotForm.confirmPassword" type="password" placeholder="请再次输入新密码" prefix-icon="Lock" show-password/>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showForgotPassword = false">取消</el-button>
          <el-button type="primary" @click="handleResetPassword">确认重置</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(-45deg, #4facfe, #00f2fe, #43e97b, #38f9d7);
  background-size: 400% 400%;
  animation: gradientMove 15s ease infinite;
  position: relative;
  overflow: hidden;
}

.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.6;
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.3);
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.2);
  top: 60%;
  left: 80%;
  animation-delay: 1s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.4);
  top: 80%;
  left: 20%;
  animation-delay: 2s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.25);
  top: 20%;
  left: 70%;
  animation-delay: 3s;
}

.shape-5 {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.35);
  top: 50%;
  left: 5%;
  animation-delay: 4s;
}

.shape-6 {
  width: 90px;
  height: 90px;
  background: rgba(255, 255, 255, 0.2);
  top: 70%;
  left: 50%;
  animation-delay: 5s;
}

.login-card {
  width: 450px;
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 24px !important;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  z-index: 10;
  overflow: hidden;
}

.login-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  padding: 30px 20px !important;
  border-bottom: none;
}

.card-header {
  text-align: center;
  color: white;
}

.logo-wrapper {
  margin-bottom: 15px;
}

.logo-img {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.title {
  font-size: 24px;
  font-weight: 700;
  margin: 10px 0 5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.login-form {
  padding: 20px 30px 10px;
}

.login-form :deep(.el-form-item__label) {
  color: #2c3e50;
  font-weight: 500;
}

.login-form :deep(.el-input__wrapper) {
  background: #f8fbff;
  border-radius: 12px;
  box-shadow: none;
  border: 2px solid #e8f4ff;
  transition: all 0.3s ease;
  padding: 5px 15px;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #4facfe;
}

.login-form :deep(.el-input__wrapper:focus-within) {
  border-color: #4facfe;
  background: white;
  box-shadow: 0 0 0 4px rgba(79, 172, 254, 0.1);
}

.captcha-wrapper {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 12px;
}

.captcha-input {
  flex: 1;
}

.captcha-img {
  height: 40px;
  border-radius: 8px;
  cursor: pointer;
  border: 2px solid #e8f4ff;
  transition: all 0.3s ease;
}

.captcha-img:hover {
  border-color: #4facfe;
  transform: scale(1.05);
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(79, 172, 254, 0.4);
}

.btn-icon {
  font-size: 18px;
}

.switch-links {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-top: 1px dashed #e8f4ff;
  margin-top: 10px;
}

.link-btn {
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s ease;
}

.link-btn:hover {
  transform: translateX(3px);
}

.forgot-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  padding: 15px 20px !important;
  margin-right: 0 !important;
}

.forgot-dialog :deep(.el-dialog__title) {
  color: white !important;
  font-weight: 600;
}

.forgot-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white !important;
}

.forgot-form {
  padding: 20px;
}

.send-code-btn {
  width: 100%;
  border-radius: 12px;
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  border: none;
  height: 40px;
  font-weight: 500;
}

.send-code-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(250, 112, 154, 0.4);
}

.send-code-btn:disabled {
  opacity: 0.7;
}

@keyframes gradientMove {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(10deg); }
}
</style>