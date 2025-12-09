<script setup>
import {reactive, ref, onMounted} from 'vue'
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router'; // 1. 引入路由钩子
import { getCaptchaAPI,loginAPI,registerAPI } from '../api/user';

const router = useRouter() // 2.以此获取路由对象

//--- 状态控制 ---
const isRegister = ref(false) // false:登录模式, true:注册模式

//定义表单数据
// --- 登录表单 ---
const loginForm = reactive({
  username: '',
  password: '',
  captchaCode: '',
  captchaKey: ''
})

// --- 注册表单 ---
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '', //确认密码
  captchaCode: '',
  captchaKey: ''
})

// 存验证码图片的 Base64
const captchaUrl = ref('')

// 获取验证码
const loadCaptcha = () => {
  getCaptchaAPI()
    .then(res => {
      if(res.data.code === 200) {
        captchaUrl.value = res.data.data.image
        loginForm.captchaKey = res.data.data.key
        registerForm.captchaKey = res.data.data.key
      }
    })
}

onMounted(() => {
  loadCaptcha()
})

//点击登录按钮触发
const handleLogin = () =>{
  if(!loginForm.username || !loginForm.password || !loginForm.captchaCode){
    ElMessage.warning('请输入完整信息')
    return
  }

  //发送POST请求给后端
  loginAPI(loginForm)
    .then(res =>{
      console.log('后端返回的数据：',res.data)
      if(res.data.code === 200){
        ElMessage.success('登录成功！欢迎你，'+res.data.data.nickname)

        //把用户信息存到浏览器缓存里
        // JSON.stringify 是把对象转成字符串
        localStorage.setItem('user_info', JSON.stringify(res.data.data))

        // 跳转到主页
        router.push('/home')
      }else{
        //密码错误或者账号被锁定
        ElMessage.error(res.data.msg)
        loadCaptcha() // 失败刷新验证码
      }
    }).catch(err=>{
      console.error(err)
      ElMessage.error('无法连接到服务器，请检查后端是否启动')
    })

}

const handleRegister = () => {
  //1.基础校验
  if(!registerForm.username || !registerForm.password || !registerForm.confirmPassword || !registerForm.captchaCode){
    ElMessage.warning('请填写完整注册信息')
    return
  }
  //2.密码一致性校验
  if(registerForm.password !== registerForm.confirmPassword){
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  //3.发送请求
  //注意：后端接口只需要username,pasword
  registerAPI({
    username: registerForm.username,
    password: registerForm.password,
    captchaCode: registerForm.captchaCode, //传验证码
    captchaKey: registerForm.captchaKey //传Key
  })
  .then(res =>{
    if(res.data.code === 200){
      ElMessage.success('注册成功！请登录')
      // 注册成功后，自动切回登录模式
      isRegister.value = false
      // 清空注册表单
      registerForm.username = ''
      registerForm.password = ''
      registerForm.confirmPassword = ''
      registerForm.captchaCode = ''
      // 注册成功后刷新验证码给登录用
      loadCaptcha()
    }else{
      ElMessage.error(res.data.msg)
      loadCaptcha() // 失败也要刷新验证码
    }
  })
}

// --- 切换模式 ---
const toggleMode = () => {
  isRegister.value = !isRegister.value
  // 切换时刷新一下验证码，清空一下表单
  loadCaptcha()
  loginForm.captchaCode = ''
  registerForm.captchaCode = ''
}

const hasLogo = ref(true)
</script>

<template>
  <div class ="login-container">
    <el-card class ="login-card">
      <template #header>
        <div class ="card-header">
          <img src="../assets/logo.png" alt="logo" class = "logo-img" v-if="hasLogo"></img>
          <h2>{{ isRegister ? '新用户注册' : '学生管理系统' }}</h2>
        </div>
      </template>

      <!--登录表单-->
      <el-form v-if="!isRegister" label-width ="80px">
        <el-form-item label ="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label ="密码">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>

        <!--验证码-->
        <el-form-item label="验证码">
            <div style="display: flex; align-items: center; width: 100%;">
                <el-input v-model="loginForm.captchaCode" placeholder="输入验证码" style="flex: 1"/>
                <!--点击图片刷新-->
                <img 
                    :src="captchaUrl" 
                    @click="loadCaptcha" 
                    style="height: 32px; margin-left: 10px; cursor: pointer; border: 1px solid #dcdfe6" 
                    title="点击刷新"
                />
            </div>
        </el-form-item>

        <el-form-item>
          <el-button type ="primary" @click="handleLogin" style="width:100%">登录</el-button>
        </el-form-item>

        <!--切换按钮-->
        <div style="text-align: right;">
          <el-button link type="primary" @click="toggleMode">没有账号？去注册</el-button>
        </div>

      </el-form>

      <!--注册表单-->
      <el-form v-else label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"/>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password/>
        </el-form-item>

        <!--验证码-->
        <el-form-item label="验证码">
            <div style="display: flex; align-items: center; width: 100%;">
                <el-input v-model="registerForm.captchaCode" placeholder="输入验证码" style="flex: 1"/>
                <!--点击图片刷新-->
                <img 
                    :src="captchaUrl" 
                    @click="loadCaptcha" 
                    style="height: 32px; margin-left: 10px; cursor: pointer; border: 1px solid #dcdfe6" 
                    title="点击刷新"
                />
            </div>
        </el-form-item>

        <el-form-item>
          <el-button type="success" @click="handleRegister" style="width: 100%">立即注册</el-button>
        </el-form-item>

        <!-- 切换按钮 -->
        <div style="text-align: right;">
          <el-button link type="primary" @click="toggleMode">已有账号？去登录</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<!-- <style>
/* body {
  margin: 0;
  padding: 0;
  overflow: hidden; /* 防止因为 margin 塌陷出现滚动条 */
} */
</style> -->

<style scoped>
.login-container{
  background: url('../assets/school_bg.jpg');
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 背景图设置 */
  background-size: cover;
  background-position: center; /*图片居中*/
  background-repeat: no-repeat;
}
.login-card{
  width: 450px;
  /* 1. 背景颜色：白色 + 透明度 (0.1 到 0.9，越小越透) */
  background-color: rgba(255, 255, 255, 0.3); 
  
  /* 2. 毛玻璃模糊效果 (数值越大越模糊) */
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px); /* 兼容 Safari */

  /* 3. 边框：加一点半透明白边，更有质感 */
  border: 1px solid rgba(255, 255, 255, 0.5);
  
  /* 4. 圆角和阴影 */
  border-radius: 15px;
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.2);

  /* === 字体颜色变深色：应用于卡片内部所有文本 === */
  color: #000000; /* 设置卡片内默认字体颜色为深灰色 */
}

.logo-img{
  width: 60px;
  height: 60px;
}

.card-header{
  text-align: center;
  color: #000000;
  font-weight: 600;
  margin: 0;
}

/* 确保 Element Plus 的 label 标签也是深色 */
:deep(.el-form-item__label) {
    color: #000000 !important; /* !important 确保覆盖 Element Plus 默认样式 */
}

:deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.8);
  box-shadow: none; /* 去掉默认边框阴影，看起来更平滑 */
  border: 1px solid #dcdfe6;
}
</style>