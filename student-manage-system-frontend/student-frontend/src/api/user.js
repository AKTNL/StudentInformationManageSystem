import request from "../utils/request";

//获取验证码
export function getCaptchaAPI(){
    return request.get('/captcha')
}

//登录
export function loginAPI(data){
    return request.post('/login', data)
}

//注册
export function registerAPI(data){
    return request.post('/register', data)
}
