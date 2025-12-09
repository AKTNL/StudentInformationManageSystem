package com.student.studentmanagesystembackend.common;

import java.security.MessageDigest;
import java.util.UUID;

public class SecureUtils {

    // 生成随机盐值（Salt）
    public static String getSalt(){
        return UUID.randomUUID().toString().substring(0,6);
    }

    // MD5加密：密码 + 盐 -> 密文
    public static String md5(String password, String salt){
        String str = password + salt;
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (int offset = 0; offset < byteDigest.length; offset++){
                i = byteDigest[offset];
                if (i < 0) i += 256;
                if (i < 16) buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
