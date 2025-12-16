import request from '../utils/request'

//选课
export function selectCourseAPI(userId, courseId) {
    return request.post(`/course-select/select?userId=${userId}&courseId=${courseId}`)
}

//退课
export function dropCourseAPI(userId, courseId) {
    return request.post(`/course-select/drop?userId=${userId}&courseId=${courseId}`)
}

//获取我的课程
export function getMyCoursesAPI(userId) {
    return request.get(`/course-select/my-courses?userId=${userId}`)
}