<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import { getCourseListAPI, addCourseAPI, deleteCourseAPI } from '../../api/course';
import VuePdfEmbed from 'vue-pdf-embed'

const tableData = ref([])
const dialogVisible = ref(false)
const previewVisible = ref(false)
const previewUrl = ref('')
const previewType = ref('')
const uploadUrl = 'http://localhost:8081/upload'

const form = reactive({
    courseName: '',
    description: '',
    coverImg: '',
    resourceUrl: '',
    resourceType: 'video',
    weekDay: 1,
    section: 1,
    location: ''
})

const loadData = () => {
    getCourseListAPI()
        .then(res => tableData.value = res.data.data)
}

onMounted(() => loadData())

const openDialog = () => {
    form.courseName = '';
    form.description = '';
    form.coverImg = '';
    form.resourceUrl = '';
    form.resourceType = 'video';
    form.weekDay = 1;
    form.section = 1;
    form.location = '';
    dialogVisible.value = true
}

// 封面上传成功
const handleCoverSuccess = (res) => {
    if(res.code === 200) form.coverImg = res.data
}

// 课件上传成功
const handleResourceSuccess = (res, file) => {
    if (res.code === 200) {
        form.resourceUrl = res.data
        // 自动判断类型
        const filename = file.name.toLowerCase();
        if (filename.endsWith('.pdf')) {
             form.resourceType = 'pdf';
        } else {
             form.resourceType = 'video'; 
        }
        ElMessage.success('课件上传成功，识别类型为：' + form.resourceType)
    } else {
        ElMessage.error('上传失败')
    }
}

const saveCourse = () => {
    if (!form.courseName || !form.resourceUrl) return ElMessage.warning('请填写完整')
    addCourseAPI(form).then(res => {
        if (res.data.code === 200) {
            ElMessage.success('发布成功')
            dialogVisible.value = false
            loadData()
        }
    })
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确定删除吗？').then(() => {
        deleteCourseAPI(row.courseId).then(res => {
            if (res.data.code === 200) {
                ElMessage.success('删除成功');
                loadData()
            }
        })
    })
}

const BASE_URL = 'http://localhost:9000/student-system/'
const handlePreview = (row) => {
    // 检查 row.resourceUrl 是否已经是完整 URL。如果不是，就拼接。
    if (row.resourceUrl && !row.resourceUrl.startsWith('http')) {
        previewUrl.value = BASE_URL + row.resourceUrl;
    } else {
        previewUrl.value = row.resourceUrl;
    }
    
    // 确保 resourceType 字段存在，如果不存在，则赋值一个默认值或通过文件名推断 (见第2点)
    previewType.value = row.resourceType || 'unknown' 
    previewVisible.value = true

    // 可以在这里再次打印最终的 URL 检查
    console.log('最终预览 URL:', previewUrl.value);
}
</script>

<template>
    <el-card>
        <template #header>
            <div class="card-header">
                <span>课程管理</span>
                <el-button type="primary" @click="openDialog">发布课程</el-button>
            </div>
        </template>

        <el-table :data="tableData" style="width: 100%" stripe border>
            <el-table-column label="封面" width="120">
                <template #default="scope">
                    <el-image :src="scope.row.coverImg" style="width: 100px; height: 60px" fit="cover"/>
                </template>
            </el-table-column>
            <el-table-column prop="courseName" label="课程名称" width="150"/>
            <el-table-column prop="description" label="简介" />
            <el-table-column prop="resourceType" label="类型" width="80">
                <template #default="scope">
                    <el-tag type="success" v-if="scope.row.resourceType === 'video'">视频</el-tag>
                    <el-tag type="warning" v-else>PDF</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
                <template #default="scope">
                    <el-button link type="primary" @click="handlePreview(scope.row)">预览内容</el-button>
                    <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 发布课程弹窗 -->
        <el-dialog v-model="dialogVisible" title="发布新课程" width="40%">
            <el-form label-width="80px">
                <el-form-item label="课程名称">
                    <el-input v-model="form.courseName" placeholder="请输入课程名称"/>
                </el-form-item>
                <el-form-item label="简介">
                    <el-input v-model="form.description" type="textarea" placeholder="简要描述"/>
                </el-form-item>

                <el-form-item label="上课时间">
                    <div class="time-select-percent-group">
                        <el-select v-model="form.weekDay" placeholder="周几" style="width: 35%;">
                            <el-option label="周一" :value="1"/>
                            <el-option label="周二" :value="2"/>
                            <el-option label="周三" :value="3"/>
                            <el-option label="周四" :value="4"/>
                            <el-option label="周五" :value="5"/>
                            </el-select>
                       
                     
                        <el-select v-model="form.section" placeholder="第几节">
                            <el-option label="第一节（08：20-09：55）" :value="1" />
                            <el-option label="第二节（10：10-11：45）" :value="2" />
                            <el-option label="第三节（13：20-14：55）" :value="3" />
                            <el-option label="第四节（15：10-16：45）" :value="4" />
                        </el-select>
                    </div>
                </el-form-item>

                <el-form-item label="上课教室">
                    <el-input v-model="form.location" placeholder="例如：教学楼 101"/>
                </el-form-item>

                <!-- 上传封面 -->
                <el-form-item label="封面图">
                    <el-upload class="avatar-uploader" :action="uploadUrl" :show-file-list="false" :on-success="handleCoverSuccess">
                        <img v-if="form.coverImg" :src="form.coverImg" class="avatar"/>
                        <el-icon v-else class="avatar-uploader-icon"><Plus/></el-icon>
                    </el-upload>
                </el-form-item>

                <!-- 上传课件 (视频或PDF) -->
                 <el-form-item label="资源类型">
                    <el-radio-group v-model="form.resourceType">
                        <el-radio label="video">视频</el-radio>
                        <el-radio label="pdf">PDF文档</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="课件文件">
                    <el-upload :action="uploadUrl" :limit="1" :on-success="handleResourceSuccess" :show-file-list="true">
                        <el-button type="primary">点击上传（视频/PDF）</el-button>
                    </el-upload>
                    <div v-if="form.resourceUrl" style="color: green; font-size: 12px; margin-top: 5px;">
                        文件已上传：{{ form.resourceUrl }}
                    </div>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="saveCourse">发布</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 预览弹窗 -->
        <el-dialog v-model="previewVisible" title="课程预览" width="70%" top="5vh">
            <div style="text-align: center; height: 70vh; overflow: auto;">
                <!-- 视频播放器 -->
                <video v-if="previewType === 'video'" :src="previewUrl" controls style="width: 100%; height: 100%; object-fit: contain;"></video>
                
                <!-- PDF 预览 -->
                <VuePdfEmbed 
                    v-if="previewType === 'pdf'" 
                    :source="previewUrl" 
                    width="800"
                />
            </div>
        </el-dialog>
    </el-card>
</template>

<style scoped>
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
}
.avatar-uploader-icon{
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    text-align: center;
    line-height: 100px;
}
.avatar{
    width: 100px;
    height: 100px;
    display: block;
}
.time-select-percent-group > .el-select {
    /* 确保下拉框并排显示，并处理它们之间的间距 */
    display: inline-block;
    margin-right: 150px; /* 添加间距 */
}

.time-select-percent-group > .el-select:last-child {
    margin-right: 0; /* 最后一个元素不需要右边距 */
}
</style>