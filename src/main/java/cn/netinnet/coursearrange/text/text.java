package cn.netinnet.coursearrange.text;

import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.util.IDUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class text {
    @Autowired
    private NinArrangeMapper ninArrangeMapper;
    @Autowired
    private NinTeacherMapper ninTeacherMapper;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinStudentMapper ninStudentMapper;
    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private NinHouseMapper ninHouseMapper;
    @Autowired
    private NinSettingMapper ninSettingMapper;
    @Autowired
    private NinTeacherCourseMapper ninTeacherCourseMapper;
    @Autowired
    private NinCareerMapper ninCareerMapper;
    @Autowired
    private NinCareerCourseMapper ninCareerCourseMapper;
    @Autowired
    private NinTeachClassMapper ninTeachClassMapper;
    @Autowired
    private NinStudentCourseMapper ninStudentCourseMapper;



    public void arrange() {
        //专业表
        List<NinCareer> ninCareers = ninCareerMapper.selectList(new QueryWrapper<>());
        List<Long> careerIdList = ninCareers.stream().map(NinCareer::getId).collect(Collectors.toList());

        //课程表
        List<NinCourse> courseList = ninCourseMapper.selectList(new QueryWrapper<>());
        Map<Long, NinCourse> longNinCourseMap = courseList.stream().collect(Collectors.toMap(NinCourse::getId, Function.identity()));

        //<专业id，班级列表>//有顺序的
        List<NinClass> ninClasses1 = ninClassMapper.selectList(new QueryWrapper<>());
        Map<Long, List<NinClass>> longListNinClassMap = ninClasses1.stream().sorted(Comparator.comparing(NinClass::getClassName)).filter(i -> i.getCareerId() != null).collect(Collectors.groupingBy(NinClass::getCareerId));

        //专业选课表
        List<NinCareerCourse> ninCareerCourses = ninCareerCourseMapper.selectList(new QueryWrapper<>());
        Map<Long, List<NinCareerCourse>> longListNinCareerCourseMap = ninCareerCourses.stream().collect(Collectors.groupingBy(NinCareerCourse::getCareerId));

        //教室表
        Map<Integer, List<NinHouse>> integerListNinHouseMap = ninHouseMapper.selectList(new QueryWrapper<>()).stream().collect(Collectors.groupingBy(NinHouse::getHouseType));

        //教师选课表
        Map<Long, List<NinTeacherCourse>> longListNinTeacherCourseMap = ninTeacherCourseMapper.selectList(new QueryWrapper<>()).stream().collect(Collectors.groupingBy(NinTeacherCourse::getCourseId));

        //存放教学班
        List<NinTeachClass> ninTeachClasses = new ArrayList<>();



        //遍历专业列表 确定教学班和课程
        for (Long careerId : careerIdList) {
            if (careerId == 0 || careerId == -1) {
                continue;
            }
            //获得该专业的专业-课程表
            List<NinCareerCourse> ninCareerCourses1 = longListNinCareerCourseMap.get(careerId);
            if (ninCareerCourses1 == null) {
                throw new ServiceException(412, "请到班级管理完成专业课程安排！");
            }

            //该专业的班级列表（有序的）
            List<NinClass> ninClasses = longListNinClassMap.get(careerId);
            if (ninClasses == null) {
                continue;
            }
            //该专业的班级数量
            Integer classNum = ninClasses.size();
            //<课程上课数量，Long{该专业分成的教学班id}>
            Map<Integer, Long[]> teachClassMap = new HashMap<>();

            //循环该专业的专业选课表
            for (NinCareerCourse ncc : ninCareerCourses1) {
                //该专业-课程记录对应的课程
                NinCourse ninCourse = longNinCourseMap.get(ncc.getCourseId());

                //最多几个班一起上课
                Integer maxClassNum = ninCourse.getMaxClassNum();

                //如果为空，生成
                if (teachClassMap.get(maxClassNum) == null) {

                    int i = classNum / maxClassNum;
                    int i1 = classNum % maxClassNum;

                    int[] ints = null;
                    if (i == 0) {
                        ints = new int[]{i1};
                    } else {
                        if (i1 == 0) {
                            //表示刚好
                            ints = grouping(classNum, i);
                        } else {
                            ints = grouping(classNum, i + 1);
                        }
                    }

                    Long[] teachClasses = new Long[ints.length];
                    for (int j = 0, count = 0; j < ints.length; j++) {
                        long teachClassId = IDUtil.getID();
                        for (int k = 0; k < ints[j]; k++, count++) {
                            Long classId = ninClasses.get(count).getId();
                            String className = ninClasses.get(count).getClassName();
                            NinTeachClass ninTeachClass = new NinTeachClass();
                            ninTeachClass.setTeachClassId(teachClassId);
                            ninTeachClass.setClassId(classId);
                            ninTeachClass.setClassName(className);

                            ninTeachClasses.add(ninTeachClass);
                        }
                        teachClasses[j] = teachClassId;
                    }
                    teachClassMap.put(maxClassNum, teachClasses);
                }

                Long[] longs = teachClassMap.get(maxClassNum);

                for (int i = 0; i < longs.length; i++) {
                    if (longs[i] == null) {
                        break;
                    }
                    NinArrange ninArrange = new NinArrange();
                    //专业id
                    ninArrange.setCareerId(careerId);
                    //教学班id
                    ninArrange.setTeachClassId(longs[i]);
                    //课程id
                    ninArrange.setCourseId(ninCourse.getId());
                    //必修
                    ninArrange.setMust(1);
                    //人数
                    Map<Long, List<NinTeachClass>> longListNinTeachClassMap = ninTeachClasses.stream().collect(Collectors.groupingBy(NinTeachClass::getTeachClassId));
                    ninArrange.setPeopleNum(longListNinTeachClassMap.get(longs[i]).size() * ApplicationConstant.CLASS_PEOPLE_NUM);

//                    ninArrangeArrayList.add(ninArrange);
                }
            }
        }












    }
    public int[] grouping(int maxNum, int minNum) {
        int[] ints = new int[minNum];
        for (int i = 0; i < maxNum; i++) {
            ints[i % ints.length]++;
        }
        return ints;
    }
}
