<script setup>
import { useRouter } from 'vue-router';
import { User, Plus } from '@element-plus/icons-vue'
import { ref, onMounted, reactive } from 'vue' // 引入 ref 和 生命周期
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  getStudentListAPI, 
  addStudentAPI, 
  updateStudentAPI, 
  deleteStudentAPI, 
  getClassListAPI 
} from '../api/student'

import request from '../utils/request';

const router = useRouter()

// --- 身份识别 ---
const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}')
const isAdmin = ref(userInfo.role === 1) // 是否是管理员

const logout = () =>{
    //退出逻辑：回到登录页
    localStorage.removeItem('user_info') // 清除缓存
    router.push('/login')
}

//定义表格数据（响应式变量）
const tableData = ref([]) // 管理员用的列表
const myInfo = ref({})    // 学生用的个人信息

// --- 加载数据 ---
const loadData = () => {
  // 加载班级字典（大家都需要）
  getClassListAPI().then(res => classList.value = res.data.data)

  if (isAdmin.value) {
    // 1. 如果是管理员，查所有
    getStudentListAPI().then(res => tableData.value = res.data.data)
  } else {
    // 2. 如果是学生，查自己
    // 注意：这里调用刚才写的新接口
    request.get('/student/my-info', { params: { userId: userInfo.userId } })
      .then(res => {
        if(res.data.code === 200) {
          myInfo.value = res.data.data
        } else {
          ElMessage.warning(res.data.msg)
        }
      })
  }
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

//打开弹窗时清空表单
const openDialog = () => {
    form.id = null // 清空ID
    form.studentNo = ''
    form.realName = ''
    form.gender = 1
    form.classId = null
    form.phone = ''
    form.avatar = ''
    form.birthday = ''
    form.email = ''

    dialogVisible.value = true
}

//保存学生
const saveStudent = () => {
    //简单校验
    if(!form.studentNo || !form.realName){
        ElMessage.warning("请填写必填项")
        return
    }

    // 判断是新增还是修改
    if(form.id){
        // --- 有ID，说明是修改 (PUT) ---
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
    }else{
        // --- 没ID，说明是新增 (POST) ---
        addStudentAPI(form)
            .then(res => {
                if(res.data.code === 200){
                    ElMessage.success('添加成功')
                    dialogVisible.value = false //关闭弹窗
                    loadData() // 重新刷新列表
                }else{
                    ElMessage.error(res.data.msg)
                }
            })
    }
}

//删除处理函数
const handleDelete = (row) => {
    //row 就是当前这一行的数据，里面有 row.id
    ElMessageBox.confirm(
        '确定要删除这位同学吗？删除后无法恢复',
        '警告',
        {
            confirmButtonText: '确认删除',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(()=>{
        //用户点击了确定，发送请求
        deleteStudentAPI(row.id)
            .then(res=>{
                if(res.data.code === 200){
                    ElMessage.success('删除成功')
                    loadData() // 刷新列表
                }else{
                    ElMessage.error(res.data.msg)
                }
        })
    }).catch(()=>{
        // 用户点击了取消，什么都不做
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

//handleEdit (管理员编辑时调用)
const handleEdit = (row) =>{
  // 把当前行的数据复制给表单
  form.id = row.id
  form.studentNo = row.studentNo
  form.realName = row.realName
  form.gender = row.gender
  form.classId = row.classId
  form.phone = row.phone
  form.avatar = row.avatar

  dialogVisible.value = true // 打开弹窗
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
                        <el-menu-item index="1">
                            <el-icon><User/></el-icon>
                            <span>{{ isAdmin ? '学生管理' : '我的档案' }}</span>
                        </el-menu-item>
                    </el-menu>
                </el-aside>

                <el-main class="main">
                    <div class="content-wrapper">
                        <!-- 卡片视图 -->
                        <el-card v-if = "isAdmin">
                            <template #header>
                                <div class = "card-header">
                                    <span>学生列表</span>
                                    <!--点击按钮打开弹窗-->
                                    <el-button type="primary" @click="openDialog">新增学生</el-button>
                                </div>
                            </template>

                            <!-- 表格区域 -->
                            <!-- :data="tableData" 意思是把变量 tableData 绑定给表格 -->
                            <el-table :data="tableData" style="width: 100%" stripe border>
                                <el-table-column label="头像" width="80">
                                    <template #default="scope">
                                        <el-avatar :src="scope.row.avatar"/>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="studentNo" label="学号" width="120"/>
                                <el-table-column prop="realName" label="姓名" width="100" />
                                <el-table-column prop="gender" label="性别" width="80">
                                    <template #default = "scope">
                                        <!-- 使用插槽把 1/2 转换成 男/女 -->
                                        <el-tag v-if="scope.row.gender === 1">男</el-tag>
                                        <el-tag v-else type="danger">女</el-tag>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="className" label="班级" width="150" />
                                <el-table-column prop="phone" label="联系电话" />
                                <el-table-column label="操作" width="150">
                                    <template #default="scope">
                                        <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
                                        <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
                                    </template>
                                </el-table-column>
                            </el-table>

                        </el-card>

                        <!-- ================= 学生视图 (v-else) ================= -->
                        
                            <el-card v-else style="width: 100%;">
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
                       

                        <!--新增学生的弹窗-->
                        <el-dialog v-model="dialogVisible" :title="form.id ? '编辑信息' : '添加学生'" width="30%">
                            <el-form label-width="80px">
                                <el-form-item label="学号">
                                    <el-input v-model="form.studentNo" :disabled="!isAdmin" placeholder="请输入学号" />
                                </el-form-item>
                                <el-form-item label="姓名">
                                    <el-input v-model="form.realName" :disabled="!isAdmin" placeholder="请输入姓名" />
                                </el-form-item>

                                <el-form-item label="头像" v-if="!isAdmin">
                                    <!-- action: 上传接口地址 -->
                                    <!-- show-file-list: false 不显示文件列表，我们自己回显图片 -->
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
                                <el-form-item label="性别">
                                    <el-radio-group v-model="form.gender" :disabled="!isAdmin">
                                        <el-radio :label="1">男</el-radio>
                                        <el-radio :label="2">女</el-radio>
                                    </el-radio-group>
                                </el-form-item>
                                <el-form-item label="班级">
                                    <el-select v-model="form.classId" :disabled="!isAdmin" placeholder="请选择班级">
                                        <!-- 动态循环 classList -->
                                        <!-- :label 显示给用户看的是 className -->
                                        <!-- :value 存到数据库的是 classId -->
                                        <el-option
                                            v-for="item in classList"
                                            :key="item.classId"
                                            :label="item.className"
                                            :value="item.classId"
                                        />
                                    </el-select>
                                </el-form-item>

                                <el-form-item label="出生日期" v-if="!isAdmin">
                                    <el-date-picker
                                        v-model="form.birthday"
                                        type="date"
                                        placeholder="选择日期"
                                        format="YYYY-MM-DD"
                                        value-format="YYYY-MM-DD"
                                        style="width: 100%"
                                    />
                                </el-form-item>

                                <el-form-item label="电子邮箱" v-if="!isAdmin">
                                    <el-input v-model="form.email" placeholder="请输入邮箱地址" />
                                </el-form-item>

                                <el-form-item label="电话" v-if="!isAdmin">
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