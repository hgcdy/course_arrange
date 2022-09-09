package cn.netinnet.coursearrange;

import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.mapper.NinClassCourseMapper;
import cn.netinnet.coursearrange.mapper.NinClassMapper;
import cn.netinnet.coursearrange.mapper.NinClassesMapper;
import cn.netinnet.coursearrange.mapper.NinCourseMapper;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.service.INinClassCourseService;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private NinClassMapper ninClassMapper;


    @Test
    void contextLoads() {
        List<NinClass> ninClasses = ninClassMapper.selectList(new QueryWrapper<NinClass>());
        System.out.println(ninClasses);
        Map<Long, List<NinClass>> collect = ninClasses.stream().collect(Collectors.groupingBy(NinClass::getCareerId));
        for (Map.Entry<Long, List<NinClass>> m :
                collect.entrySet()) {
            m.getValue().get(0).setId(1L);
        }
        System.out.println(ninClasses);


    }

    public int[] grouping(int maxNum, int minNum){
        int[] ints = new int[minNum];
        for (int i = 0; i < maxNum; i++) {
            ints[i % ints.length]++;
        }
        return ints;
    }


}


