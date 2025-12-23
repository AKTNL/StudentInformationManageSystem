package com.student.studentmanagesystembackend.context;

import com.student.studentmanagesystembackend.entity.User;

public class UserContext {
    private static final ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void set(User user){
        userHolder.set(user);
    }

    public static User get(){
        return userHolder.get();
    }

    public static void remove(){
        userHolder.remove();
    }
}
