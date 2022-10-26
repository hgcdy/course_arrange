package cn.netinnet.coursearrange.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/application.yml")
public class ApplicationConstant {

    //管理员账号
    public static String ADMIN_NAME;
    //管理员账号
    public static String ADMIN_CODE;
    //管理员密码
    public static String ADMIN_PASSWORD;

    //角色类型
    public static String TYPE_ADMIN = "admin";
    public static String TYPE_TEACHER = "teacher";
    public static String TYPE_STUDENT = "student";
    public static String TYPE_CLASS = "class";

    //选修课默认的时间  {4,3} -> 周四第三节课
    public static int[][] TASK_COURSE_TIME = {{4, 3}, {4, 4}, {5, 3}, {5, 4}};
    //班级人数上限
    public static int CLASS_PEOPLE_NUM = 50;
    //教师选课数量上限
    public static int TEACHER_COURSE_NUM = 2;
    //学生选课数量上限
    public static int STUDENT_COURSE_NUM = 2;


    @Value("${project.admin.name}")
    public void setAdminName(String adminName) {
        ApplicationConstant.ADMIN_NAME = adminName;
    }

    @Value("${project.admin.code}")
    public void setAdminCode(String adminCode) {
        ApplicationConstant.ADMIN_CODE = adminCode;
    }

    @Value("${project.admin.password}")
    public void setAdminPassword(String adminPassword) {
        ApplicationConstant.ADMIN_PASSWORD = adminPassword;
    }


}
