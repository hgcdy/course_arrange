package cn.netinnet.coursearrange;


import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.service.*;
import cn.netinnet.coursearrange.util.NameUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class CourseArrangeApplicationTests {

//@Autowired
//private INinTeacherService ninTeacherService;
//@Autowired
//private INinClassService ninClassService;
//@Autowired
//private INinStudentService ninStudentService;
//@Autowired
//private INinTeacherCourseService ninTeacherCourseService;
//@Autowired
//private INinCourseService ninCourseService;
//@Autowired
//private INinCareerCourseService ninCareerCourseService;

//    @Test
//    void text() {
//        for (int i = 1; i < 100; i++) {
//            NinTeacher ninTeacher = new NinTeacher();
//            ninTeacher.setTeacherPassword("123456");
//            ninTeacher.setTeacherCode(String.format("t%03d", i));
//            ninTeacher.setTeacherName(NameUtil.getRandName());
//            ninTeacherService.addSingle(ninTeacher);
//        }
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put("数据", "s01%s%02d");
//        map.put("物联", "s02%s%02d");
//        map.put("软件", "s03%s%02d");
//        map.put("智能", "s04%s%02d");
//        map.put("临床", "s05%s%02d");
//        map.put("护理", "s06%s%02d");
//
//
//
//        List<NinClass> list = ninClassService.list();
//        for (NinClass clazz : list) {
//            if (clazz.getCareerId() == 0 || clazz.getCareerId() == -1) {
//                continue;
//            }
//            String className = clazz.getClassName();
//            String substring = className.substring(0, 2);
//            String substring1 = className.substring(2, 3);
//            String s = map.get(substring);
//            for (int i = 1; i < 51; i++) {
//                NinStudent ninStudent = new NinStudent();
//                ninStudent.setStudentPassword("123456");
//                ninStudent.setStudentName(NameUtil.getRandName());
//                ninStudent.setClassId(clazz.getId());
//                ninStudent.setStudentCode(String.format(s, substring1, i));
//
//                ninStudentService.addSingle(ninStudent);
//            }
//        }
//
//        List<NinCourse> list1 = ninCourseService.list();
//        Map<Long, Integer> collect = list1.stream().collect(Collectors.toMap(NinCourse::getId, i -> i.getCourseTime() > 32 ? 2 : 1));
//        List<NinCareerCourse> list2 = ninCareerCourseService.list();
//        Map<Long, Long> collect1 = list2.stream().collect(Collectors.groupingBy(NinCareerCourse::getCourseId, Collectors.counting()));
//        for (Map.Entry<Long, Long> map5 : collect1.entrySet()) {
//            Long key = map5.getKey();
//            Long value = map5.getValue();
//
//            Integer integer = collect.get(key);
//            int i = value.intValue() * integer;
//            int ceil = (int) Math.ceil(i / 4.0);
//
//            collect.put(key, ceil);
//        }
//
//        List<NinTeacher> list3 = ninTeacherService.list();
//        int count = 0;
//        for (Map.Entry<Long, Integer> map4: collect.entrySet()){
//            Integer value = map4.getValue();
//            Long key = map4.getKey();
//            for (int i = 0; i < value; i++) {
//                ninTeacherCourseService.addSingle(list3.get(count).getId(), key);
//                count++;
//            }
//        }
//    }


}


