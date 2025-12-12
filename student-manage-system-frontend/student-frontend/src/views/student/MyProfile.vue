<script setup>
import { useRouter } from 'vue-router';
import { User, Plus } from '@element-plus/icons-vue'
import { ref, onMounted, reactive } from 'vue' // 引入 ref 和 生命周期
import { ElMessage } from 'element-plus';
import { 
  updateStudentAPI, 
  getClassListAPI 
} from '../../api/student'

import request from '../../utils/request';

const router = useRouter()

// --- 身份识别 ---
const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}')

const logout = () =>{
    //退出逻辑：回到登录页
    localStorage.removeItem('user_info') // 清除缓存
    router.push('/login')
}

//定义表格数据（响应式变量）
const myInfo = ref({})    // 学生用的个人信息

// --- 加载数据 ---
const loadData = () => {
  // 加载班级字典（大家都需要）
  getClassListAPI().then(res => classList.value = res.data.data)

    // 加载个人信息
  request.get('/student/my-info', { params: { userId: userInfo.userId } })
    .then(res => {
        if(res.data.code === 200) {
          myInfo.value = res.data.data
        } else {
          ElMessage.warning(res.data.msg)
        }
    })
  
}

//页面加载完成后，自动调用
onMounted(() => {
    if(!userInfo.userId) {
        ElMessage.error('未登录')
        router.push('/login')
        return
    }
    loadData()
})

// 控制弹窗显示隐藏的变量
const dialogVisible = ref(false)

// 表单数据
const form = reactive({
    id: null, // 新增时是null，修改时是有值的
    studentNo: '',
    realName: '',
    gender: 1,
    classId: null,
    phone: '',
    avatar: '',
    birthday: '',
    email: ''
})

//保存学生
const saveStudent = () => {
    //简单校验
    updateStudentAPI(form)
            .then(res => {
                if(res.data.code === 200){
                    ElMessage.success('修改成功')
                    dialogVisible.value = false //关闭弹窗
                    loadData() // 重新刷新列表
                }else{
                    ElMessage.error(res.data.msg)
                }
            })
}


// --- 学生的操作：编辑自己 ---
const handleEditSelf = () => {
  // 把自己的信息填入表单
  form.id = myInfo.value.id
  form.studentNo = myInfo.value.studentNo
  form.realName = myInfo.value.realName
  form.gender = myInfo.value.gender
  form.classId = myInfo.value.classId
  form.phone = myInfo.value.phone
  form.avatar = myInfo.value.avatar
  form.birthday = myInfo.value.birthday
  form.email = myInfo.value.email
  
  dialogVisible.value = true
}

//定义班级列表
const classList = ref([])

//获取班级列表的方法
const loadClasses = () =>{
    getClassListAPI()
        .then(res => {
            if(res.data.code === 200){
                classList.value = res.data.data
            }
        })
}

const handleAvatarSuccess = (response) =>{
    // response 就是后端返回的 Result 对象
    if(response.code === 200){
        form.avatar = response.data //把后端返回的 URL 填入表单
        ElMessage.success('头像上传成功')
    }else{
        ElMessage.error('上传失败')
    }
}

const uploadUrl = 'http://localhost:8081/upload'
</script>

<template>
    <div class = "common-layout">
        <el-container>
            
            <el-container>

                <el-main class="main">
                    <div class="content-wrapper">
                        <!-- 卡片视图 -->
                            <el-card style="width: 100%;">
                                <template #header>
                                    <div class="card-header">
                                        <span>我的个人档案</span>
                                        <el-button type="primary" link @click="handleEditSelf">修改资料</el-button>
                                    </div>
                                </template>
                                
                                <div style="text-align: center; margin-bottom: 20px;">
                                    <el-avatar :size="100" :src="myInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                                    <h2>{{ myInfo.realName }}</h2>
                                </div>

                                <el-descriptions :column="1" border>
                                    <el-descriptions-item label="学号">{{ myInfo.studentNo }}</el-descriptions-item>
                                    <el-descriptions-item label="班级">{{ myInfo.className || '暂无班级' }}</el-descriptions-item>
                                    <el-descriptions-item label="性别">
                                        <el-tag v-if="myInfo.gender === 1">男</el-tag>
                                        <el-tag v-else type="danger">女</el-tag>
                                    </el-descriptions-item>
                                    <!-- 【新增】生日和邮箱 -->
                                    <el-descriptions-item label="出生日期">{{ myInfo.birthday || '未填写' }}</el-descriptions-item>
                                    <el-descriptions-item label="电子邮箱">{{ myInfo.email || '未填写' }}</el-descriptions-item>
                                    <el-descriptions-item label="联系电话">{{ myInfo.phone }}</el-descriptions-item>
                                    <el-descriptions-item label="注册时间">{{ myInfo.createTime }}</el-descriptions-item>
                                </el-descriptions>
                            </el-card>
                       
                        <el-dialog v-model="dialogVisible" title="编辑信息" width="30%">
                            <el-form label-width="80px">

                                <el-form-item label="头像">
                                    <!-- action: 上传接口地址 -->
                                    <!-- show-file-list: false 不显示文件列表，回显图片 -->
                                    <!-- on-success: 上传成功后的回调 -->
                                    <el-upload
                                        class="avatar-uploader"
                                        :action = uploadUrl
                                        :show-file-list="false"
                                        :on-success="handleAvatarSuccess"
                                    >
                                        <!-- 如果有头像，显示图片；没有则显示加号 -->
                                        <img v-if="form.avatar" :src="form.avatar" class="avatar" />
                                        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                                    </el-upload>
                                </el-form-item>

                                <el-form-item label="出生日期">
                                    <el-date-picker
                                        v-model="form.birthday"
                                        type="date"
                                        placeholder="选择日期"
                                        format="YYYY-MM-DD"
                                        value-format="YYYY-MM-DD"
                                        style="width: 100%"
                                    />
                                </el-form-item>

                                <el-form-item label="电子邮箱">
                                    <el-input v-model="form.email" placeholder="请输入邮箱地址" />
                                </el-form-item>

                                <el-form-item label="电话">
                                    <el-input v-model="form.phone" placeholder="请输入电话"/>
                                </el-form-item>
                            </el-form>
                            <template #footer>
                                <span class="dialog-footer">
                                    <el-button @click="dialogVisible = false">取消</el-button>
                                    <el-button type="primary" @click="saveStudent">确定</el-button>
                                </span>
                            </template>
                        </el-dialog>
                    </div>

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