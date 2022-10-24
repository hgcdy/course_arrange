package cn.netinnet.coursearrange;

import cn.netinnet.coursearrange.mapper.NinArrangeMapper;
import cn.netinnet.coursearrange.mapper.NinClassMapper;
import cn.netinnet.coursearrange.mapper.NinCourseMapper;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    void contextLoads() {

        List<Integer> integers = new ArrayList<>();
        HashSet<Integer> integers1 = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            integers.add(i);
            integers1.add(i);
        }
        System.out.println(integers);
        System.out.println(integers1);


        System.out.println("");
    }

}


