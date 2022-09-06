package cn.netinnet.coursearrange;

import cn.netinnet.coursearrange.mapper.NinClassCourseMapper;
import cn.netinnet.coursearrange.mapper.NinCourseMapper;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.service.INinClassCourseService;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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


    @Test
    void contextLoads() {
        ninArrangeService.arrange();


    }

}


