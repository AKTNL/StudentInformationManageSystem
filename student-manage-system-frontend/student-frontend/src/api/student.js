import request from "../utils/request";

//获取学生列表
export function getStudentListAPI(){
    return request.get('/students')
}

//新增学生
export function addStudentAPI(data){
    return request.post('/students', data)
}

// 修改学生
export function updateStudentAPI(data) {
  return request.put('/students', data)
}

// 删除学生
export function deleteStudentAPI(id) {
  return request.delete(`/students/${id}`)
}

// 获取班级列表
export function getClassListAPI() {
  return request.get('/classes')
}