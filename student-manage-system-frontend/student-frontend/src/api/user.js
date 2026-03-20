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

//发送密码重置验证码
export function sendResetCodeAPI(data){
    return request.post('/send-reset-code', data)
}

//重置密码
export function resetPasswordAPI(data){
    return request.post('/reset-password', data)
}
