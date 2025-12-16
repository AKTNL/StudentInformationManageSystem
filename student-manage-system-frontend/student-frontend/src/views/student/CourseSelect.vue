<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import { getCourseListAPI } from '../../api/course';
import { selectCourseAPI, dropCourseAPI, getMyCoursesAPI } from '../../api/selection'

const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}')
const allCourses = ref([])
const myCourses = ref([])

//加载数据
const loadData = async () => {
    //1.获取所有课程
    const resl = await getCourseListAPI()
    if (resl.data.code === 200) allCourses.value = resl.data.data

    //2.获取我已选的课程
    const res2 = await getMyCoursesAPI(userInfo.userId)
    if(res2.data.code === 200) myCourses.value = res2.data.data
}

onMounted(() => loadData())

//判断某门课是否在“我的课程”里
const isSelected = (courseId) => {
    return myCourses.value.some(c => c.courseId === courseId)
}

//选课
const handleSelect = (course) => {
    selectCourseAPI(userInfo.userId, course.courseId).then(res => {
        if (res.data.code === 200) {
            ElMessage.success('选课成功')
            loadData() // 刷新数据
        } else {
            ElMessage.error(res.data.msg)
        }
    })
}

//退课
const handleDrop = (course) => {
    ElMessageBox.confirm(`确定要退选《${course.courseName}》吗？`, '警告', { type: 'warning' })
        .then(() => {
            dropCourseAPI(userInfo.userId, course.courseId).then(res => {
                if (res.data.code === 200) {
                    ElMessage.success('退课成功')
                    loadData()
                }
            })
        })
}
</script>

<template>
    <div style="padding: 20px;">

        <!--1.我的课表-->
        <el-card class="box-card" style="margin-bottom: 20px">
            <template #header>
                <div class="card-header">
                    <span>📅 我的已选课程</span>
                </div>
            </template>
            <el-table :data="myCourses" style="width: 100%" empty-text="暂无选课">
                <el-table-column prop="courseName" label="课程名称"/>
                <el-table-column prop="description" label="简介"/>
                <el-table-column label="操作" width="120">
                    <template #default="scope">
                        <el-button type="danger" size="small" @click="handleDrop(scope.row)">退课</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!--2.选课大厅-->
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <span>📚 选课中心 (所有课程)</span>
                </div>
            </template>

            <el-row :gutter="20">
                <el-col :span="6" v-for="course in allCourses" :key="course.courseId" style="margin-bottom: 20px;">
                    <el-card :body-style="{ padding: '0px' }" shadow="hover">
                        <img :src="course.coverImg" class="image" style="width: 100%; height: 150px; object-fit: cover;"/>
                        <div style="padding: 14px;">
                            <span style="font-weight: bold;">{{ course.courseName }}</span>
                            <div class="bottom">
                                <span class="desc">{{ course.description }}</span>
                                <el-button v-if="isSelected(course.courseId)" type="info" disabled size="small">已选</el-button>
                                <el-button v-else type="primary" size="small" @click="handleSelect(course)">选课</el-button>
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </el-card>
    </div>
</template>

<style scoped>
.desc{
    font-size: 13px;
    color: #999;
    display: block;
    margin-top: 5px;
    height: 40px;
    overflow: hidden;
}
.bottom{
    margin-top: 13px;
    line-height: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>