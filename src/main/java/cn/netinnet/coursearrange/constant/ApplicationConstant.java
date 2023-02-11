package cn.netinnet.coursearrange.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/application.yml")
public class ApplicationConstant {

    public static Long ADMIN_ID;
    //管理员名称
    public static String ADMIN_NAME;
    //管理员账号
    public static String ADMIN_CODE;
    //管理员密码
    public static String ADMIN_PASSWORD;


    public static String PROJECT_CODE;


    //班级人数上限
    public static int CLASS_PEOPLE_NUM = 50;
    //教师选课数量上限
    public static int TEACHER_COURSE_NUM = 2;
    //学生选课数量上限
    public static int STUDENT_COURSE_NUM = 2;
    //一天的课程数
    public static int DAY_PITCH_NUM = 5;


    @Value("${project.admin.id}")
    public void setAdminName(Long adminId) {
        ApplicationConstant.ADMIN_ID = adminId;
    }

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

    @Value("${project.projectCode}")
    public void setProjectCode(String projectCode) {
        ApplicationConstant.PROJECT_CODE = projectCode;
    }


}
