package cn.netinnet.coursearrange;

import cn.netinnet.coursearrange.config.WebSocketServer;
import cn.netinnet.coursearrange.entity.NinArrange;
import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.mapper.NinArrangeMapper;
import cn.netinnet.coursearrange.mapper.NinClassMapper;
import cn.netinnet.coursearrange.mapper.NinCourseMapper;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import cn.netinnet.coursearrange.util.CnUtil;
import cn.netinnet.coursearrange.util.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.*;


@SpringBootTest
class CourseArrangeApplicationTests {
    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private INinStudentCourseService ninStudentCourseService;
    @Autowired
    private INinArrangeService ninArrangeService;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinArrangeMapper ninArrangeMapper;

    /**
     * 1、学院专业增删改查和专业批量选课页面
     * 4、限制排课前需要教师选完课程，开放学生选课需要排课后
     * 5、教室申请重做
     */

    /**
     * 2、优化排课代码
     * 6、申请教室管理员同意添加
     */


    /**
     * 管理员修改用户选课，给用户发送消息
     * 选课开始结束发送消息
     * 教师申请课程，发送给管理员，等待确认
     * 确认后，返回给教师
     */

    @Test
    void contextLoads() {

    }

    /**
     * 班级和课程形成教学班-->教学任务  任务等级（1234）-两周一节到一周两节
     * 教师分配教学任务，使得教师的教学任务等级总数平均
     *
     *
     *
     *
     *
     */


}


