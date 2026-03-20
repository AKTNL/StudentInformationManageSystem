<script setup>
import { useRouter } from 'vue-router';
import { User, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  getStudentListAPI, 
  addStudentAPI, 
  updateStudentAPI, 
  deleteStudentAPI, 
  getClassListAPI 
} from '../../api/student'

const router = useRouter()

const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}')

const logout = () =>{
    localStorage.removeItem('user_info')
    router.push('/login')
}

const tableData = ref([])

const loadData = () => {
  getClassListAPI().then(res => classList.value = res.data)
  getStudentListAPI().then(res => tableData.value = res.data)
}

onMounted(() => loadData())

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

const openDialog = () => {
    form.id = null
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

const saveStudent = () => {
    if(!form.studentNo || !form.realName){
        ElMessage.warning("请填写必填项")
        return
    }

    if(form.id){
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
    }else{
        addStudentAPI(form)
            .then(res => {
                ElMessage.success('添加成功')
                dialogVisible.value = false
                loadData()
            })
            .catch(err => {
                console.error(err)
                ElMessage.error('添加失败')
            })
    }
}

const handleDelete = (row) => {
    ElMessageBox.confirm(
        '确定要删除这位同学吗？删除后无法恢复',
        '警告',
        {
            confirmButtonText: '确认删除',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(()=>{
        deleteStudentAPI(row.id)
            .then(res=>{
                ElMessage.success('删除成功')
                loadData()
            })
            .catch(err => {
                console.error(err)
                ElMessage.error('删除失败')
            })
    }).catch(()=>{
    })
}

const handleEdit = (row) =>{
  form.id = row.id
  form.studentNo = row.studentNo
  form.realName = row.realName
  form.gender = row.gender
  form.classId = row.classId
  form.phone = row.phone
  form.avatar = row.avatar
  form.birthday = row.birthday 
  form.email = row.email

  dialogVisible.value = true
}

const classList = ref([])

const loadClasses = () =>{
    getClassListAPI()
        .then(res => {
            classList.value = res.data
        })
}
</script>

<template>
    <div class="student-list-page">
        <div class="page-header">
            <div class="header-info">
                <h2 class="page-title">学生管理</h2>
                <p class="page-desc">管理所有学生信息，支持新增、编辑和删除操作</p>
            </div>
            <el-button type="primary" class="add-btn" @click="openDialog">
                <el-icon><Plus /></el-icon>
                新增学生
            </el-button>
        </div>

        <el-card class="table-card">
            <el-table :data="tableData" style="width: 100%" stripe>
                <el-table-column label="头像" width="80">
                    <template #default="scope">
                        <el-avatar :src="scope.row.avatar" class="student-avatar">
                            {{ scope.row.realName?.charAt(0) }}
                        </el-avatar>
                    </template>
                </el-table-column>
                <el-table-column prop="studentNo" label="学号" width="120"/>
                <el-table-column prop="realName" label="姓名" width="100" />
                <el-table-column prop="gender" label="性别" width="80">
                    <template #default="scope">
                        <el-tag :type="scope.row.gender === 1 ? '' : 'danger'" class="gender-tag">
                            {{ scope.row.gender === 1 ? '男' : '女' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="className" label="班级" width="150">
                    <template #default="scope">
                        <span class="class-name">{{ scope.row.className }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="phone" label="联系电话" />
                <el-table-column label="操作" width="180">
                    <template #default="scope">
                        <div class="action-btns">
                            <el-button type="primary" size="small" class="action-btn edit-btn" @click="handleEdit(scope.row)">
                                <el-icon><Edit /></el-icon>
                                编辑
                            </el-button>
                            <el-button type="danger" size="small" class="action-btn delete-btn" @click="handleDelete(scope.row)">
                                <el-icon><Delete /></el-icon>
                                删除
                            </el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="dialogVisible" :title="form.id ? '编辑信息' : '添加学生'" width="500px" class="form-dialog">
            <el-form label-width="80px" class="student-form">
                <el-form-item label="学号">
                    <el-input v-model="form.studentNo" placeholder="请输入学号" prefix-icon="Postcard" />
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input v-model="form.realName" placeholder="请输入姓名" prefix-icon="User" />
                </el-form-item>
                <el-form-item label="性别">
                    <el-radio-group v-model="form.gender" class="gender-radio">
                        <el-radio :label="1">男</el-radio>
                        <el-radio :label="2">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="班级">
                    <el-select v-model="form.classId" placeholder="请选择班级" class="class-select">
                        <el-option v-for="item in classList" :key="item.classId" :label="item.className" :value="item.classId"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="电话">
                    <el-input v-model="form.phone" placeholder="请输入电话" prefix-icon="Phone" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
                    <el-button type="primary" @click="saveStudent" class="confirm-btn">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<style scoped>
.student-list-page {
    width: 100%;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 20px 24px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(79, 172, 254, 0.08);
}

.header-info {
    flex: 1;
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

.add-btn {
    height: 44px;
    padding: 0 24px;
    font-size: 15px;
    font-weight: 600;
    border-radius: 12px;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    border: none;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 8px;
}

.add-btn:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(79, 172, 254, 0.4);
}

.table-card {
    border-radius: 16px;
    border: none;
    box-shadow: 0 2px 12px rgba(79, 172, 254, 0.08);
}

.table-card :deep(.el-card__body) {
    padding: 0;
}

.student-avatar {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: white;
    font-weight: 600;
}

.gender-tag {
    border-radius: 20px;
    font-weight: 500;
}

.class-name {
    color: #4facfe;
    font-weight: 500;
}

.action-btns {
    display: flex;
    gap: 8px;
}

.action-btn {
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 4px;
}

.edit-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
}

.delete-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(250, 112, 154, 0.3);
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

.student-form {
    padding: 20px;
}

.student-form :deep(.el-input__wrapper) {
    background: #f8fbff;
    border-radius: 10px;
    border: 2px solid #e8f4ff;
    transition: all 0.3s ease;
}

.student-form :deep(.el-input__wrapper:hover),
.student-form :deep(.el-input__wrapper:focus-within) {
    border-color: #4facfe;
    background: white;
}

.gender-radio {
    display: flex;
    gap: 20px;
}

.class-select {
    width: 100%;
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
</style>