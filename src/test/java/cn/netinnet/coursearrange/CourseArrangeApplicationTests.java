package cn.netinnet.coursearrange;

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
     */

    /**
     * 2、优化排课代码
     * 3、优化sql代码
     * 5、教室申请重做
     * 6、申请教室管理员同意添加
     * 7、util.navPath('/排课管理/教室申请');点击跳转
     */

    @Test
    void contextLoads() {


    }



}


