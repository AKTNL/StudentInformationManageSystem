<script setup>
import { useRouter } from 'vue-router';
import { User, Plus, Edit, Postcard, Calendar, Message, Phone, Clock } from '@element-plus/icons-vue'
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus';
import { 
  updateStudentAPI, 
  getClassListAPI 
} from '../../api/student'

import request from '../../utils/request';

const router = useRouter()

const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}')

const logout = () =>{
    localStorage.removeItem('user_info')
    router.push('/login')
}

const myInfo = ref({})

const loadData = () => {
  getClassListAPI().then(res => classList.value = res.data)

  request.get('/student/my-info', { params: { userId: userInfo.userId } })
    .then(res => {
      myInfo.value = res.data
    })
    .catch(err => {
      console.error(err)
      ElMessage.warning('获取个人信息失败')
    })
}

onMounted(() => {
    if(!userInfo.userId) {
        ElMessage.error('未登录')
        router.push('/login')
        return
    }
    loadData()
})

const dialogVisible = ref(false)

const form = reactive({
    id: null,
    studentNo: '',
    realName: '',
    gender: 1,
    classId: null,
    phone: '',
    avatar: '',
    birthday: '',
    email: ''
})

const saveStudent = () => {
    updateStudentAPI(form)
        .then(res => {
            ElMessage.success('修改成功')
            dialogVisible.value = false
            loadData()
        })
        .catch(err => {
            console.error(err)
            ElMessage.error('修改失败')
        })
}

const handleEditSelf = () => {
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

const classList = ref([])

const loadClasses = () =>{
    getClassListAPI()
        .then(res => {
            classList.value = res.data
        })
}

const handleAvatarSuccess = (response) =>{
    if(response.code === 200){
        form.avatar = response.data
        ElMessage.success('头像上传成功')
    }else{
        ElMessage.error('上传失败')
    }
}

const uploadUrl = 'http://localhost:8081/upload'
</script>

<template>
    <div class="profile-page">
        <div class="profile-header">
            <h2 class="page-title">我的档案</h2>
            <p class="page-desc">查看和管理您的个人信息</p>
        </div>

        <el-card class="profile-card">
            <div class="profile-content">
                <div class="avatar-section">
                    <div class="avatar-wrapper">
                        <el-avatar :size="120" :src="myInfo.avatar" class="profile-avatar">
                            {{ myInfo.realName?.charAt(0) }}
                        </el-avatar>
                        <div class="avatar-badge"></div>
                    </div>
                    <h2 class="profile-name">{{ myInfo.realName }}</h2>
                    <p class="profile-class">{{ myInfo.className || '暂无班级' }}</p>
                </div>

                <div class="info-section">
                    <div class="info-grid">
                        <div class="info-item">
                            <div class="info-icon">
                                <el-icon><Postcard /></el-icon>
                            </div>
                            <div class="info-content">
                                <span class="info-label">学号</span>
                                <span class="info-value">{{ myInfo.studentNo }}</span>
                            </div>
                        </div>

                        <div class="info-item">
                            <div class="info-icon gender-icon">
                                <el-icon><User /></el-icon>
                            </div>
                            <div class="info-content">
                                <span class="info-label">性别</span>
                                <el-tag :type="myInfo.gender === 1 ? '' : 'danger'" class="gender-tag">
                                    {{ myInfo.gender === 1 ? '男' : '女' }}
                                </el-tag>
                            </div>
                        </div>

                        <div class="info-item">
                            <div class="info-icon birthday-icon">
                                <el-icon><Calendar /></el-icon>
                            </div>
                            <div class="info-content">
                                <span class="info-label">出生日期</span>
                                <span class="info-value">{{ myInfo.birthday || '未填写' }}</span>
                            </div>
                        </div>

                        <div class="info-item">
                            <div class="info-icon email-icon">
                                <el-icon><Message /></el-icon>
                            </div>
                            <div class="info-content">
                                <span class="info-label">电子邮箱</span>
                                <span class="info-value">{{ myInfo.email || '未填写' }}</span>
                            </div>
                        </div>

                        <div class="info-item">
                            <div class="info-icon phone-icon">
                                <el-icon><Phone /></el-icon>
                            </div>
                            <div class="info-content">
                                <span class="info-label">联系电话</span>
                                <span class="info-value">{{ myInfo.phone || '未填写' }}</span>
                            </div>
                        </div>

                        <div class="info-item">
                            <div class="info-icon time-icon">
                                <el-icon><Clock /></el-icon>
                            </div>
                            <div class="info-content">
                                <span class="info-label">注册时间</span>
                                <span class="info-value">{{ myInfo.createTime }}</span>
                            </div>
                        </div>
                    </div>

                    <el-button type="primary" class="edit-btn" @click="handleEditSelf">
                        <el-icon><Edit /></el-icon>
                        修改资料
                    </el-button>
                </div>
            </div>
        </el-card>

        <el-dialog v-model="dialogVisible" title="编辑信息" width="500px" class="form-dialog">
            <el-form label-width="80px" class="profile-form">
                <el-form-item label="头像">
                    <el-upload
                        class="avatar-uploader"
                        :action="uploadUrl"
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess"
                    >
                        <img v-if="form.avatar" :src="form.avatar" class="avatar-preview" />
                        <div v-else class="avatar-placeholder">
                            <el-icon class="upload-icon"><Plus /></el-icon>
                            <span>上传头像</span>
                        </div>
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
                    <el-input v-model="form.email" placeholder="请输入邮箱地址" prefix-icon="Message" />
                </el-form-item>

                <el-form-item label="电话">
                    <el-input v-model="form.phone" placeholder="请输入电话" prefix-icon="Phone" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
                    <el-button type="primary" @click="saveStudent" class="confirm-btn">保存修改</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<style scoped>
.profile-page {
    width: 100%;
    max-width: 900px;
    margin: 0 auto;
}

.profile-header {
    margin-bottom: 24px;
    padding: 20px 24px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(79, 172, 254, 0.08);
}

.page-title {
    font-size: 22px;
    font-weight: 700;
    margin: 0 0 6px;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

.page-desc {
    font-size: 14px;
    color: #7f8c8d;
    margin: 0;
}

.profile-card {
    border-radius: 20px;
    border: none;
    box-shadow: 0 4px 20px rgba(79, 172, 254, 0.1);
    overflow: hidden;
}

.profile-content {
    display: flex;
    gap: 40px;
    padding: 30px;
}

.avatar-section {
    text-align: center;
    min-width: 200px;
}

.avatar-wrapper {
    position: relative;
    display: inline-block;
    margin-bottom: 20px;
}

.profile-avatar {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: white;
    font-size: 48px;
    font-weight: 600;
    border: 4px solid white;
    box-shadow: 0 8px 30px rgba(79, 172, 254, 0.3);
}

.avatar-badge {
    position: absolute;
    bottom: 5px;
    right: 5px;
    width: 24px;
    height: 24px;
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
    border-radius: 50%;
    border: 3px solid white;
}

.profile-name {
    font-size: 24px;
    font-weight: 700;
    color: #2c3e50;
    margin: 0 0 8px;
}

.profile-class {
    font-size: 14px;
    color: #7f8c8d;
    margin: 0;
}

.info-section {
    flex: 1;
}

.info-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    margin-bottom: 30px;
}

.info-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    background: #f8fbff;
    border-radius: 12px;
    transition: all 0.3s ease;
}

.info-item:hover {
    background: #e8f4ff;
    transform: translateX(5px);
}

.info-icon {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 20px;
}

.gender-icon {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.birthday-icon {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.email-icon {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.phone-icon {
    background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.time-icon {
    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
    color: #666;
}

.info-content {
    flex: 1;
}

.info-label {
    display: block;
    font-size: 12px;
    color: #7f8c8d;
    margin-bottom: 4px;
}

.info-value {
    font-size: 15px;
    font-weight: 600;
    color: #2c3e50;
}

.gender-tag {
    border-radius: 20px;
}

.edit-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 12px;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    border: none;
    transition: all 0.3s ease;
}

.edit-btn:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(79, 172, 254, 0.4);
}

.form-dialog :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    padding: 16px 20px !important;
    margin-right: 0 !important;
}

.form-dialog :deep(.el-dialog__title) {
    color: white !important;
    font-weight: 600;
}

.form-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
    color: white !important;
}

.profile-form {
    padding: 20px;
}

.profile-form :deep(.el-input__wrapper) {
    background: #f8fbff;
    border-radius: 10px;
    border: 2px solid #e8f4ff;
    transition: all 0.3s ease;
}

.profile-form :deep(.el-input__wrapper:hover),
.profile-form :deep(.el-input__wrapper:focus-within) {
    border-color: #4facfe;
    background: white;
}

.avatar-uploader :deep(.el-upload) {
    border: 2px dashed #e8f4ff;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    overflow: hidden;
}

.avatar-uploader :deep(.el-upload:hover) {
    border-color: #4facfe;
    background: #f8fbff;
}

.avatar-preview {
    width: 120px;
    height: 120px;
    display: block;
    object-fit: cover;
}

.avatar-placeholder {
    width: 120px;
    height: 120px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #7f8c8d;
}

.upload-icon {
    font-size: 32px;
    margin-bottom: 8px;
}

.cancel-btn {
    border-radius: 10px;
    padding: 10px 24px;
}

.confirm-btn {
    border-radius: 10px;
    padding: 10px 24px;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    border: none;
    font-weight: 600;
}

.confirm-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
}

@media (max-width: 768px) {
    .profile-content {
        flex-direction: column;
        align-items: center;
    }

    .info-grid {
        grid-template-columns: 1fr;
    }
}
</style>