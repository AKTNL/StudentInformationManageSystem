package com.student.studentmanagesystembackend.interceptor;

import com.student.studentmanagesystembackend.context.UserContext;
import com.student.studentmanagesystembackend.entity.User;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Properties;

@Component
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class })
})
public class DataPermissionInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable{
        //1.获取当前登录用户
        User user = UserContext.get();

        // 如果没人登录，或者他是管理员(role=1)，或者是学生(role=2, 学生走自己的API)，直接放行
        // 我们只拦截 老师 (role=3)
        if (user == null || user.getRole() != 3){
            return invocation.proceed();
        }

        //2.获取原始 SQL
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String originalSql = boundSql.getSql();

        //3.解析SQL
        Statement statement = CCJSqlParserUtil.parse(originalSql);

        //我们只处理select语句
        if (statement instanceof Select){
            Select select = (Select) statement;
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

            //4.判断是否是查students表
            String tableName = plainSelect.getFromItem().toString();
            if (tableName.equalsIgnoreCase("students")){
                EqualsTo equalsTo = new EqualsTo();
                equalsTo.setLeftExpression(new Column("class_id"));
                equalsTo.setRightExpression(new LongValue(user.getManagedClassId()));

                Expression originalWhere = plainSelect.getWhere();
                if (originalWhere == null){
                    plainSelect.setWhere(equalsTo);
                }else{
                    AndExpression andExpression = new AndExpression(originalWhere, equalsTo);
                    plainSelect.setWhere(andExpression);
                }

                String newSql = select.toString();
                java.lang.reflect.Field field = boundSql.getClass().getDeclaredField("sql");
                field.setAccessible(true);
                field.set(boundSql, newSql);

                System.out.println("【数据权限拦截】原始SQL: " + originalSql);
                System.out.println("【数据权限拦截】新SQL: " + newSql);

            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target){
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties){}
}
