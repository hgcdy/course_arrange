package cn.netinnet.coursearrange;

import cn.netinnet.coursearrange.mapper.NinClassCourseMapper;
import cn.netinnet.coursearrange.mapper.NinClassesMapper;
import cn.netinnet.coursearrange.mapper.NinCourseMapper;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.service.INinClassCourseService;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class CourseArrangeApplicationTests {
    @Autowired
    private NinClassCourseMapper ninClassCourseMapper;
    @Autowired
    private INinClassCourseService ninClassCourseService;
    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private INinStudentCourseService ninStudentCourseService;
    @Autowired
    private INinArrangeService ninArrangeService;
    @Autowired
    private NinClassesMapper ninClassesMapper;


    @Test
    void contextLoads() {
//        ninArrangeService.arrange();
        List<Long> l = new ArrayList<>();
        l.add(90233863826545481L);
        List<Long> classesIdList = ninClassesMapper.getClassesIdList(l);
        System.out.println("11");

    }

}


