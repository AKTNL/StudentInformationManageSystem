<script setup>
import { useRouter } from 'vue-router';
import { User, VideoPlay, Collection, Calendar, Bell, SwitchButton } from '@element-plus/icons-vue'
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { ElMessage, ElNotification } from 'element-plus';
import StudentList from './admin/StudentList.vue';
import MyProfile from './student/MyProfile.vue';
import CourseList from './admin/CourseList.vue';
import CourseSelect from './student/CourseSelect.vue';
import CourseSchedule from './student/CourseSchedule.vue';

const router = useRouter()

const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}')
const role = userInfo.role

const isAdmin = ref(role === 1)
const isTeacher = ref(role === 3)
const isStudent = ref(role === 2)

const canManage = computed(()=>isAdmin.value || isTeacher.value)

const currentComponent = ref(canManage.value ? 'StudentList' : 'MyProfile')

const logout = () =>{
    localStorage.removeItem('user_info')
    router.push('/login')
}

const componentsMap = {
    StudentList,
    CourseList,
    MyProfile,
    CourseSelect,
    CourseSchedule
}

const pageTitles = {
    StudentList: '学生管理',
    CourseList: '课程管理',
    MyProfile: '我的档案',
    CourseSelect: '选课中心',
    CourseSchedule: '我的课表'
}

const getPageTitle = () => {
    return pageTitles[currentComponent.value] || '首页'
}

let socket = null

const initWebSocket = () => {
    if (typeof (WebSocket) === "undefined") {
        console.error("您的浏览器不支持WebSocket")
        return
    }

    const wsUrl = `ws://localhost:8081/ws/${userInfo.userId}`

    socket = new WebSocket(wsUrl)

    socket.onopen = () => {
        console.log("WebSocket连接已建立")
    }

    socket.onmessage = (msg) => {
        console.log("收到消息：", msg.data)
        ElNotification({
            title: '系统通知',
            message: msg.data,
            type: 'success',
            duration: 5000
        })
    }

    socket.onclose = () => {
        console.log("WebSocket连接已关闭")
    }

    socket.onerror = () => {
        console.log("WebSocket发生错误")
    }
}

onMounted(() => {
    if (!userInfo.userId) {
        ElMessage.error('未登录')
        router.push('/login')
        return
    }

    initWebSocket()
})

onBeforeUnmount(() => {
    if(socket) {
        socket.close()
    }
})

const getRoleName = () => {
    if(isAdmin.value) return '管理员'
    if (isTeacher.value) return '教师'
    return '学生'
}
</script>

<template>
    <div class="common-layout">
        <el-container>
            <el-aside width="240px" class="aside">
                <div class="logo-section">
                    <div class="logo-icon">🎓</div>
                    <div class="logo-text">
                        <div class="logo-title">学生管理系统</div>
                        <div class="logo-subtitle">{{ canManage ? '管理端' : '学生端' }}</div>
                    </div>
                </div>
                
                <el-menu 
                    :default-active="currentComponent" 
                    class="side-menu"
                    :collapse="false"
                >
                    <template v-if="canManage">
                        <el-menu-item index="StudentList" @click="currentComponent = 'StudentList'">
                            <el-icon><User/></el-icon>
                            <span>学生管理</span>
                        </el-menu-item>
                        <el-menu-item index="CourseList" v-if="isAdmin" @click="currentComponent = 'CourseList'">
                            <el-icon><VideoPlay/></el-icon>
                            <span>课程管理</span>
                        </el-menu-item>
                    </template>

                    <template v-else>
                        <el-menu-item index="MyProfile" @click="currentComponent = 'MyProfile'">
                            <el-icon><User/></el-icon>
                            <span>我的档案</span>
                        </el-menu-item>
                        <el-menu-item index="CourseSelect" @click="currentComponent = 'CourseSelect'">
                            <el-icon><Collection/></el-icon>
                            <span>选课中心</span>
                        </el-menu-item>
                        <el-menu-item index="CourseSchedule" @click="currentComponent = 'CourseSchedule'">
                            <el-icon><Calendar/></el-icon>
                            <span>我的课表</span>
                        </el-menu-item>
                    </template>
                </el-menu>

                <div class="user-card">
                    <div class="user-avatar">{{ userInfo.nickname?.charAt(0) || 'U' }}</div>
                    <div class="user-info">
                        <div class="user-name">{{ userInfo.nickname }}</div>
                        <div class="user-role">{{ getRoleName() }}</div>
                    </div>
                    <el-button type="danger" size="small" class="logout-btn" @click="logout">
                        <el-icon><SwitchButton /></el-icon>
                    </el-button>
                </div>
            </el-aside>

            <el-container>
                <el-header class="header">
                    <div class="header-left">
                        <h2 class="page-title">{{ getPageTitle() }}</h2>
                    </div>
                    <div class="header-right">
                        <el-badge :value="0" class="notification-badge">
                            <el-button :icon="Bell" circle class="header-btn" />
                        </el-badge>
                        <div class="user-profile">
                            <span class="user-greeting">你好，{{ userInfo.nickname }}</span>
                        </div>
                    </div>
                </el-header>

                <el-main class="main">
                    <transition name="fade" mode="out-in">
                        <component :is="componentsMap[currentComponent]" :key="currentComponent" />
                    </transition>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<style scoped>
.common-layout {
    height: 100vh;
    overflow: hidden;
}

.aside {
    height: 100vh;
    background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
    display: flex;
    flex-direction: column;
    border-right: none;
    box-shadow: 4px 0 20px rgba(0, 0, 0, 0.1);
}

.logo-section {
    padding: 30px 20px;
    display: flex;
    align-items: center;
    gap: 15px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
    font-size: 40px;
    animation: float 3s ease-in-out infinite;
}

.logo-text {
    flex: 1;
}

.logo-title {
    font-size: 18px;
    font-weight: 700;
    color: white;
    margin-bottom: 4px;
}

.logo-subtitle {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.6);
}

.side-menu {
    flex: 1;
    border: none;
    background: transparent;
    padding: 10px 0;
}

.side-menu :deep(.el-menu-item) {
    color: rgba(255, 255, 255, 0.8);
    margin: 4px 12px;
    border-radius: 12px;
    transition: all 0.3s ease;
}

.side-menu :deep(.el-menu-item:hover) {
    background: rgba(255, 255, 255, 0.1);
    color: white;
    transform: translateX(5px);
}

.side-menu :deep(.el-menu-item.is-active) {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: white;
    box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
}

.side-menu :deep(.el-menu-item .el-icon) {
    font-size: 18px;
    margin-right: 8px;
}

.user-card {
    padding: 20px;
    background: rgba(0, 0, 0, 0.2);
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    display: flex;
    align-items: center;
    gap: 12px;
}

.user-avatar {
    width: 45px;
    height: 45px;
    border-radius: 50%;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 18px;
    font-weight: 600;
    box-shadow: 0 4px 10px rgba(79, 172, 254, 0.3);
}

.user-info {
    flex: 1;
}

.user-name {
    color: white;
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 2px;
}

.user-role {
    color: rgba(255, 255, 255, 0.6);
    font-size: 12px;
}

.logout-btn {
    background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
    border: none;
    width: 36px;
    height: 36px;
    padding: 0;
    border-radius: 50%;
    transition: all 0.3s ease;
}

.logout-btn:hover {
    transform: scale(1.1) rotate(180deg);
    box-shadow: 0 4px 15px rgba(250, 112, 154, 0.4);
}

.header {
    background: white;
    box-shadow: 0 2px 20px rgba(0, 0, 0, 0.05);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 30px;
    height: 64px;
}

.header-left {
    display: flex;
    align-items: center;
}

.page-title {
    font-size: 20px;
    font-weight: 700;
    margin: 0;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 20px;
}

.notification-badge {
    cursor: pointer;
}

.header-btn {
    background: #f8fbff;
    border: 2px solid #e8f4ff;
    color: #4facfe;
    transition: all 0.3s ease;
}

.header-btn:hover {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    border-color: transparent;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(79, 172, 254, 0.3);
}

.user-profile {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 16px;
    background: #f8fbff;
    border-radius: 20px;
    border: 2px solid #e8f4ff;
    transition: all 0.3s ease;
}

.user-profile:hover {
    background: white;
    border-color: #4facfe;
    box-shadow: 0 4px 15px rgba(79, 172, 254, 0.2);
}

.user-greeting {
    font-size: 14px;
    color: #2c3e50;
    font-weight: 500;
}

.main {
    background: linear-gradient(135deg, #f5f7fa 0%, #e8f4ff 100%);
    padding: 24px;
    overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
    opacity: 0;
    transform: translateY(10px);
}

.fade-leave-to {
    opacity: 0;
    transform: translateY(-10px);
}

@keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-5px); }
}

.content-wrapper {
    width: 100%;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.avatar-uploader .el-upload {
    border: 2px dashed #d9d9d9;
    border-radius: 12px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s ease;
}

.avatar-uploader .el-upload:hover {
    border-color: #4facfe;
    background: #f8fbff;
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    text-align: center;
    line-height: 100px;
}

.avatar {
    width: 100px;
    height: 100px;
    display: block;
    border-radius: 12px;
}
</style>