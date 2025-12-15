import request from "../utils/request";

export function getCourseListAPI() {
    return request.get('/courses')
}

export function addCourseAPI(data) {
    return request.post('/courses', data)
}

export function deleteCourseAPI(id) {
    return request.delete(`/courses/${id}`)
}