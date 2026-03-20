-- 学生信息管理系统数据库优化索引

-- 用户表索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);

-- 学生表索引
CREATE INDEX idx_students_user_id ON students(user_id);
CREATE INDEX idx_students_student_no ON students(student_no);
CREATE INDEX idx_students_class_id ON students(class_id);

-- 课程表索引
CREATE INDEX idx_courses_create_time ON courses(create_time);
CREATE INDEX idx_courses_week_day ON courses(week_day);

-- 选课表索引
CREATE INDEX idx_student_course_student_id ON student_course(student_id);
CREATE INDEX idx_student_course_course_id ON student_course(course_id);

-- 角色权限关联表索引
CREATE INDEX idx_role_permission_role_id ON sys_role_permission(role_id);
CREATE INDEX idx_role_permission_permission_id ON sys_role_permission(permission_id);

-- 班级表索引
CREATE INDEX idx_classes_class_name ON classes(class_name);
