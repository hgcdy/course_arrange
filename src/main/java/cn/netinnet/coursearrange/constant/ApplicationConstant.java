package cn.netinnet.coursearrange.constant;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/application.yml")
public class ApplicationConstant {

    /**
     * 选修课默认的时间
     */
    public static int[][] TASK_COURSE_TIME = {{4,3}, {4,4}, {5,3}, {5,4}};

    /**
     * 班级人数
     */
    public static int CLASS_PEOPLE_NUM = 50;

    /**
     * 教师最多选课数量
     */
    public static int TEACHER_COURSE_NUM = 2;

    /**
     * 学生最多选课数量
     */
    public static int STUDENT_COURSE_NUM = 2;






}
