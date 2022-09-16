package cn.netinnet.coursearrange;

import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.mapper.NinArrangeMapper;
import cn.netinnet.coursearrange.mapper.NinClassCourseMapper;
import cn.netinnet.coursearrange.mapper.NinClassMapper;
import cn.netinnet.coursearrange.mapper.NinCourseMapper;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.service.INinClassCourseService;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
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
    @Autowired
    private NinArrangeMapper ninArrangeMapper;


    @Test
    void contextLoads() {
//     ninArrangeService.arrange();
//        Map<String, String> info = ninArrangeService.getInfo(null, null, 1L, null);
//        System.out.println(info);
//        List<Map<String, Object>> info = ninArrangeMapper.getInfo(null, null, 3L);
//        ArrayList<Long> classIdList = new ArrayList<>();
//        classIdList.add(100L);
//        ArrayList<Long> teachClassIdList = new ArrayList<>();
//        teachClassIdList.add(812322783474005866L);
//
//        List<Map<String, Object>> info1 = ninArrangeMapper.getInfo(classIdList, null, null);
//        List<Map<String, Object>> info2 = ninArrangeMapper.getInfo(null, teachClassIdList, null);
//        List<Map<String, Object>> info3 = ninArrangeMapper.getInfo(classIdList, teachClassIdList, null);

//        List<NinClass> cds = ninClassMapper.getSelectList("新工科产业学院", null, null);

        List<Map<String, Object>> maps = ninClassMapper.collegeCareerClassList();
        Map<String, Map<String, List<Map<String, Object>>>> collect = maps.stream().collect(Collectors.groupingBy(i -> (String) (i.get("college")), Collectors.groupingBy(i -> (String) i.get("careerName"))));

        System.out.println("");

    }

    public int[] grouping(int maxNum, int minNum){
        int[] ints = new int[minNum];
        for (int i = 0; i < maxNum; i++) {
            ints[i % ints.length]++;
        }
        return ints;
    }


}


