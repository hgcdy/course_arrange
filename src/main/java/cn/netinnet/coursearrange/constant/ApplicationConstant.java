package cn.netinnet.coursearrange.constant;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/application.yml")
public class ApplicationConstant {

    /**
     * 选修课默认的时间
     */
    public static int[][] TASK_COURSE_TIME = {{4,3}, {4,4}};






}
