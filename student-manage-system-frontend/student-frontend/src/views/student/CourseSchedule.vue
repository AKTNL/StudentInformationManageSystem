<script setup>
import { ref, onMounted } from 'vue'
import { getMyCoursesAPI } from '../../api/selection';

const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}')
const scheduleData = ref([])

// 初始化空课表 (4行)
const initSchedule = () => {
    const sections = ['第一节（08：20）', '第二节（10：10）', '第三节（13：20）', '第四节（15：10）']
    return sections.map((name, index) => ({
        sectionName: name,
        sectionIndex: index + 1,
        mon: null,
        tue: null,
        wed: null,
        thu: null,
        fri: null
    }))
}

const loadData = async () => {
    //1.获取我选的所有课
    const res = await getMyCoursesAPI(userInfo.userId)
    if (res.data.code === 200) {
        const myCourses = res.data.data

        //2.转换数据格式
        const tempSchedule = initSchedule()

        //3.遍历课程，填入表格
        myCourses.forEach(course => {
            // weekDay: 1-5 (对应 mon-fri), section: 1-4 (对应数组索引 0-3)
            const rowIndex = course.section - 1
            if (rowIndex >= 0 && rowIndex < 4) {
                const row = tempSchedule[rowIndex]
                // 根据 weekDay 决定填入哪一列
                switch (course.weekDay) {
                    case 1: row.mon = course; break;
                    case 2: row.tue = course; break;
                    case 3: row.wed = course; break;
                    case 4: row.thu = course; break;
                    case 5: row.fri = course; break;
                }
            }
        })
        scheduleData.value = tempSchedule
    }
}

onMounted(() => loadData())

// 样式美化
const cellStyle = ({ row, column, rowIndex, columnIndex }) => {
    // 计划给有课的格子加个背景色
    return ''
}
</script>

<template>
    <el-card>
        <template #header>
            <div class="card-header">
                <span>📅 我的课程表</span>
                <el-tag type="info">Tips: 每天4个大节</el-tag>
            </div>
        </template>
        
        <!--课表表格-->
        <el-table :data="scheduleData" border style="width: 100%" :cell-style="cellStyle">
            <el-table-column prop="sectionName" label="节次/时间" width="150" align="center"/>
            <el-table-column prop="mon" label="周一" align="center">
                <template #default="scope">
                    <div v-if="scope.row.mon" class="course-box">
                        <div class="name">{{ scope.row.mon.courseName }}</div>
                        <div class="loc">@{{ scope.row.mon.location }}</div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="tue" label="周二" align="center">
                <template #default="scope">
                    <div v-if="scope.row.tue" class="course-box">
                        <div class="name">{{ scope.row.tue.courseName }}</div>
                        <div class="loc">@{{ scope.row.tue.location }}</div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="wed" label="周三" align="center">
                <template #default="scope">
                    <div v-if="scope.row.wed" class="course-box">
                        <div class="name">{{ scope.row.wed.courseName }}</div>
                        <div class="loc">@{{ scope.row.wed.location }}</div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="thu" label="周四" align="center">
                <template #default="scope">
                    <div v-if="scope.row.thu" class="course-box">
                        <div class="name">{{ scope.row.thu.courseName }}</div>
                        <div class="loc">@{{ scope.row.thu.location }}</div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="fri" label="周五" align="center">
                <template #default="scope">
                    <div v-if="scope.row.fri" class="course-box">
                        <div class="name">{{ scope.row.fri.courseName }}</div>
                        <div class="loc">@{{ scope.row.fri.location }}</div>
                    </div>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>

<style scoped>
.card-header{
    display: flex;
    justify-self: space-between;
    align-items: center;
}
.course-box{
    background-color: #ecf5ff;
    border-radius: 4px;
    padding: 8px;
    color: #409eff;
    border: 1px solid #d9ecff;
}
.name{
    font-weight: bold;
    font-size: 14px;
}
.loc{
    font-size: 12px;
    margin-top: 5px;
    color: #666;
}
</style>