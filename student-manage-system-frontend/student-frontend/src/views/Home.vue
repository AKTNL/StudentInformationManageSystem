<script setup>
import { useRouter } from 'vue-router';
import { User, VideoPlay, Collection } from '@element-plus/icons-vue'
import { ref } from 'vue' // 引入 ref 
import StudentList from './admin/StudentList.vue';
import MyProfile from './student/MyProfile.vue';
import CourseList from './admin/CourseList.vue';
import CourseSelect from './student/CourseSelect.vue';

const router = useRouter()

// --- 身份识别 ---
const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}')
const isAdmin = ref(userInfo.role === 1) // 是否是管理员

// 如果是管理员，默认看 StudentList；如果是学生，默认看 MyProfile
const currentComponent = ref(isAdmin.value ? 'StudentList' : 'MyProfile') // 默认显示学生列表/学生管理

const logout = () =>{
    //退出逻辑：回到登录页
    localStorage.removeItem('user_info') // 清除缓存
    router.push('/login')
}

const componentsMap = {
    StudentList,
    CourseList,
    MyProfile,
    CourseSelect
}
</script>

<template>
    <div class = "common-layout">
        <el-container>
            <el-header class = "header">
                <span>🎓学生信息管理系统 - {{ isAdmin ? '管理员端' : '学生端' }}</span>
                <div style="display: flex; align-items: center;">
                    <span style="margin-right: 15px; font-size: 14px">
                        {{ userInfo.nickname }} ({{ isAdmin ? '管理员' : '学生' }})
                    </span>
                    <el-button type="danger" size="small" @click="logout">退出</el-button>
                </div>
            </el-header> 
            <el-container>
                <el-aside width="200px" class="aside">
                    <el-menu default-active="1" class="el-menu-vertical-demo">
                        <!-- ================= 管理员菜单 (v-if="isAdmin") ================= -->
                        <template v-if="isAdmin">
                            <el-menu-item index="1" @click="currentComponent = 'StudentList'">
                                <el-icon><User/></el-icon>
                                <span>学生管理</span>
                            </el-menu-item>
                            <el-menu-item index="2" @click="currentComponent = 'CourseList'">
                                <el-icon><VideoPlay/></el-icon>
                                <span>课程管理</span>
                            </el-menu-item>
                        </template>

                        <!-- ================= 学生菜单 (v-else) ================= -->
                        <template v-else>
                            <el-menu-item index="1" @click="currentComponent = 'MyProfile'">
                                <el-icon><User/></el-icon>
                                <span>我的档案</span>
                            </el-menu-item>
                            <el-menu-item index="2" @click="currentComponent = 'CourseSelect'">
                                <el-icon><Collection/></el-icon>
                                <span>选课中心</span>
                            </el-menu-item>
                        </template>
                    </el-menu>
                </el-aside>

                <el-main class="main">
                    <component :is="componentsMap[currentComponent]" />
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<style scoped>
.header{
    background-color: #409EFF;
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 20px;
}
.aside{
    height: calc(100vh - 60px);
    border-right: 1px solid #e6e6e6;
}
.main{
    background-color: #f5f7fa;
    padding: 20px;
    display: flex;
    flex-direction: column;
}

.content-wrapper {
  width: 100%;
}

.card-header{
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.avatar-uploader .el-upload{
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}
.avatar-upload .el-upload:hover{
    border-color: #409eff;
}
.avatar-uploader-icon{
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    text-align: center;
    line-height: 100px; /* 垂直居中 */
}
.avatar{
    width: 100px;
    height: 100px;
    display: block;
}
</style>