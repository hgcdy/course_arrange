package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@Service
public class NinArrangeServiceImpl extends ServiceImpl<NinArrangeMapper, NinArrange> implements INinArrangeService {

    @Autowired
    private NinArrangeMapper ninArrangeMapper;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinStudentMapper ninStudentMapper;
    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private NinHouseMapper ninHouseMapper;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void arrange() {
        long oldData = System.currentTimeMillis();
        //获取选修的排课记录
        List<NinArrange> ninArrangeList = ninArrangeMapper.selectList(new QueryWrapper<NinArrange>().eq("must", 0));

        //专业表
        List<NinCareer> ninCareers = ninCareerMapper.selectList(new QueryWrapper<>());
        List<Long> careerIdList = ninCareers.stream().map(NinCareer::getId).collect(Collectors.toList());

        //课程表
        List<NinCourse> courseList = ninCourseMapper.selectList(new QueryWrapper<>());
        Map<Long, NinCourse> longNinCourseMap = courseList.stream().collect(Collectors.toMap(NinCourse::getId, Function.identity()));

        //<专业id，班级列表>//有顺序的
        List<NinClass> ninClasses1 = ninClassMapper.selectList(new QueryWrapper<NinClass>());
        Map<Long, List<NinClass>> longListNinClassMap = ninClasses1.stream().filter(i -> i.getCareerId() != null).collect(Collectors.groupingBy(NinClass::getCareerId));

        //专业选课表
        List<NinCareerCourse> ninCareerCourses = ninCareerCourseMapper.selectList(new QueryWrapper<>());
        Map<Long, List<NinCareerCourse>> longListNinCareerCourseMap = ninCareerCourses.stream().collect(Collectors.groupingBy(NinCareerCourse::getCareerId));

        //教室表
        Map<Integer, List<NinHouse>> integerListNinHouseMap = ninHouseMapper.selectList(new QueryWrapper<>()).stream().collect(Collectors.groupingBy(NinHouse::getHouseType));

        //教师选课表
        Map<Long, List<NinTeacherCourse>> longListNinTeacherCourseMap = ninTeacherCourseMapper.selectList(new QueryWrapper<>()).stream().collect(Collectors.groupingBy(NinTeacherCourse::getCourseId));

        //存放教学班
        List<NinTeachClass> ninTeachClasses = new ArrayList<>();

        //必修的排课列表
        ArrayList<NinArrange> ninArrangeArrayList = new ArrayList<>();

        //遍历专业列表 确定教学班和课程
        for (Long careerId : careerIdList) {
            if (careerId == 0) {
                continue;
            }
            //获得该专业的专业-课程表
            List<NinCareerCourse> ninCareerCourses1 = longListNinCareerCourseMap.get(careerId);

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
                    for (int j = 0; j < ints.length; j++) {
                        long id = IDUtil.getID();
                        for (int k = 0; k < ints[j]; k++) {
                            NinTeachClass ninTeachClass = new NinTeachClass();
                            ninTeachClass.setTeachClassId(id);
                            ninTeachClass.setClassId(ninClasses.get(j + k).getId());
                            ninTeachClass.setCreateUserId(UserUtil.getUserInfo().getUserId());
                            ninTeachClass.setModifyUserId(UserUtil.getUserInfo().getUserId());
                            ninTeachClasses.add(ninTeachClass);
                        }
                        teachClasses[j] = id;
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
                    ninArrange.setPeopleNum(longListNinTeachClassMap.get(longs[i]).size() * 50);
                    //创建修改者id
                    ninArrange.setCreateUserId(UserUtil.getUserInfo().getUserId());
                    ninArrange.setModifyUserId(UserUtil.getUserInfo().getUserId());

                    ninArrangeArrayList.add(ninArrange);
                }
            }
        }

        //按专业排序、课程分组
        //Map<课程id，List<排课记录>>
        List<NinArrange> ninArranges = ninArrangeArrayList.stream().sorted(Comparator.comparing(NinArrange::getCareerId)).collect(Collectors.toList());
        Map<Long, List<NinArrange>> courseListArrangeMap = ninArranges.stream().collect(Collectors.groupingBy(NinArrange::getCourseId));

        //确定教师
        for (Map.Entry<Long, List<NinArrange>> map : courseListArrangeMap.entrySet()) {
            //课程
            NinCourse ninCourse = longNinCourseMap.get(map.getKey());
            //选择该课程的教师(教师-课程表记录)
            List<NinTeacherCourse> ninTeacherCourses = longListNinTeacherCourseMap.get(map.getKey());
            if (ninTeacherCourses == null) {
                //没有教师授课
                break;
            }

            //教师id列表
            List<Long> teacherIdList = ninTeacherCourses.stream().map(NinTeacherCourse::getTeacherId).collect(Collectors.toList());

            //有选择该课程的教学班(排课记录)数量
            int size = map.getValue().size();

            //确定教师
            if (teacherIdList.size() >= size) {
                //如果教师数量比较多，大于大于教学班数量
                for (int i = 0; i < size; i++) {
                    map.getValue().get(i).setTeacherId(teacherIdList.get(i));
                }
            } else {
                //如果教师数量少
                int[] grouping = grouping(size, teacherIdList.size());
                for (int i = 0; i < grouping.length; i++) {
                    for (int j = 0; j < grouping[i]; j++) {
                        map.getValue().get(i + j).setTeacherId(teacherIdList.get(i));
                    }
                }
            }

        }

        //确定教室
        for (NinArrange arrange : ninArranges) {
            Long courseId = arrange.getCourseId();
            NinCourse ninCourse = longNinCourseMap.get(courseId);
            Integer houseType = ninCourse.getHouseType();
            if (houseType == 3 || houseType == 4) {
                //3-课外，4-网课，对应id也是3和4
                arrange.setHouseId((long) houseType);
            } else {
                //符合教室类型，大于班级人数的教室,排序
                //todo 教室最多可容纳的人数小于该教学班的人数，添加课程时提醒
                List<NinHouse> collect = integerListNinHouseMap.get(houseType).stream().filter(i -> i.getSeat() >= arrange.getPeopleNum()).collect(Collectors.toList());
                Integer seat = collect.stream().min(Comparator.comparing(NinHouse::getSeat)).get().getSeat();
                List<NinHouse> ninHouses = collect.stream().collect(Collectors.groupingBy(NinHouse::getSeat)).get(seat);
                int i = (int) (Math.random() * ninHouses.size());
                arrange.setHouseId(ninHouses.get(i).getId());
            }
        }

        //按每周需要上的节数降序排序
        List<NinCourse> collect = courseList.stream().filter(i -> i.getMust() == 1).sorted(Comparator.comparing(i -> (double) i.getCourseTime() / 2 / i.getWeekTime(), Comparator.reverseOrder())).collect(Collectors.toList());

        //专业排序，课程分组
        Map<Long, List<NinArrange>> longListNinArrangeMap = ninArranges.stream().sorted(Comparator.comparing(NinArrange::getCareerId)).collect(Collectors.groupingBy(NinArrange::getCourseId));

        Map<Long, List<NinTeachClass>> longListNinTeachClassMap = ninTeachClasses.stream().collect(Collectors.groupingBy(NinTeachClass::getTeachClassId));
        //确定时间
        for (NinCourse course : collect) {
            //每周要上的课程
            double v = (double) course.getCourseTime() / 2 / course.getWeekTime();
            if (v <= 0.5) {
                //两周一节
                v = 0.5;
            } else if (v <= 1) {
                //一周一节
                v = 1;
            } else if (v <= 1.5) {
                //两周三节
                v = 1.5;
            } else {
                //v,向上取整
                v = Math.ceil(v);
            }

            //该课程的排课记录
            List<NinArrange> ninArranges1 = longListNinArrangeMap.get(course.getId());

            for (NinArrange arrange : ninArranges1) {
                //一周要上复数课程的，先随机插入，一定次数后按顺序排
                if (Math.ceil(v) >= 2) {
                    Integer weekTime = course.getWeekTime();
                    Integer startTime = course.getStartTime();
                    Integer endTime = course.getEndTime();
                    int i1 = (int) (Math.random() * ((endTime - startTime + 1) / weekTime));
                    arrange.setStartTime(startTime + i1 * weekTime);
                    arrange.setEndTime(arrange.getStartTime() + weekTime - 1);
                    for (int i = 0; i < (int) Math.ceil(v); i++) {
                        NinArrange arrange1 = new NinArrange();
                        BeanUtils.copyProperties(arrange, arrange1);
                        int count = 0;
                        do {
                            arrange1.setWeekly(0);
                            if (v == 1.5 && i == 0) {
                                arrange1.setWeekly((int) (Math.random() * 2) + 1);
                            }

                            arrange1.setWeek((int) (Math.random() * 5) + 1);
                            arrange1.setPitchNum((int) (Math.random() * 5) + 1);
                            if (count++ > 100) {
                                break;
                            }

                        } while (!compare(ninArrangeList, arrange1, longListNinTeachClassMap));
                        if (count > 100) {
                            ok: for (int j = 0; j < 7; j++) {
                                for (int k = 0; k < 5; k++) {
                                    arrange1.setWeek(j + 1);
                                    arrange1.setPitchNum(k + 1);
                                    if (compare(ninArrangeList, arrange1, longListNinTeachClassMap)) {
                                        break ok;
                                    }
                                }
                                //如果循环结束。。置空
                                arrange1.setWeekly(null);
                                arrange1.setWeek(null);
                                arrange1.setPitchNum(null);
                                arrange1.setStartTime(null);
                                arrange1.setEndTime(null);
                            }
                        }
                        //id
                        arrange1.setId(IDUtil.getID());
                        ninArrangeList.add(arrange1);
                    }
                } else {
                    NinArrange arrange1 = new NinArrange();
                    BeanUtils.copyProperties(arrange, arrange1);
                    Integer weekTime = course.getWeekTime();
                    Integer startTime = course.getStartTime();
                    Integer endTime = course.getEndTime();
                    int i1 = (int) (Math.random() * ((endTime - startTime + 1) / weekTime));
                    arrange1.setStartTime(startTime + i1 * weekTime);

                    //id
                    arrange1.setId(IDUtil.getID());
                    if (v == 1) {
                        arrange1.setEndTime(arrange1.getStartTime() + weekTime - 1);
                        arrange1.setWeekly(0);
                        ok:
                        for (int j = 0; j < 7; j++) {
                            for (int k = 0; k < 5; k++) {
                                arrange1.setWeek(j + 1);
                                arrange1.setPitchNum(k + 1);
                                if (compare(ninArrangeList, arrange1, longListNinTeachClassMap)) {
                                    break ok;
                                }
                            }
                        }
                    } else if (v == 0.5) {
                        arrange1.setEndTime(arrange1.getStartTime() + course.getCourseTime() - 1);
                        ok:
                        for (int j = 0; j < 7; j++) {
                            for (int k = 0; k < 5; k++) {
                                for (int i = 0; i < 2; i++) {
                                    arrange1.setWeek(j + 1);
                                    arrange1.setPitchNum(k + 1);
                                    arrange1.setWeekly(i + 1);
                                    if (compare(ninArrangeList, arrange1, longListNinTeachClassMap)) {
                                        break ok;
                                    }
                                }
                            }
                        }

                    }
                    ninArrangeList.add(arrange1);
                }

            }

        }

        //删除教学班表
        ninTeachClassMapper.delete(new QueryWrapper<>());
        //生成教学班级表
        ninTeachClassMapper.addBatch(ninTeachClasses);
        //删除排课记录表
        ninArrangeMapper.delete(new QueryWrapper<>());
        //生成排课记录表
        int len = ninArrangeList.size();
        for (int i = 0; i < len; i += 500) {
            if (i + 500 > len) {
                ninArrangeMapper.addBatch(ninArrangeList.subList(i, len));
            } else {
                ninArrangeMapper.addBatch(ninArrangeList.subList(i, i + 500));
            }
        }
        long newData = System.currentTimeMillis();
        System.out.println("----排课结束,用时" + (newData - oldData) + "毫秒----");

    }


    @Override
    public Map<String, String> getInfo(Long classId, Long teacherId, Long studentId, Integer count) {
        List<Map<String, Object>> info = new ArrayList<>();
        if (teacherId != null) {
            //教师
            info = ninArrangeMapper.getInfo(null, null, teacherId);
        } else if (classId != null) {
            //班级(选修)
            List<Long> longs = new ArrayList<>();
            longs.add(classId);
            info = ninArrangeMapper.getInfo(longs, null, null);
            if (info.size() == 0) {
                //如果为空，则为必修班级
                longs.remove(0);
                longs = ninTeachClassMapper.getTeachClassIdList(classId);
                info = ninArrangeMapper.getInfo(null, longs, null);
            }
        } else if (studentId != null) {
            //学生
            //学生的选修班级
            List<Long> classIdList = new ArrayList<>();
            List<NinStudentCourse> ninStudentCourses = ninStudentCourseMapper.selectList(new QueryWrapper<>(new NinStudentCourse() {{
                setStudentId(getStudentId());
            }}));
            if (ninStudentCourses != null && ninStudentCourses.size() != 0) {
                for (NinStudentCourse nsc : ninStudentCourses) {
                    classIdList.add(nsc.getTakeClassId());
                }
            }
            //学生的行政班级-》教学班级列表
            NinStudent ninStudent = ninStudentMapper.selectById(studentId);
            List<Long> teachClassIdList = ninTeachClassMapper.getTeachClassIdList(ninStudent.getClassId());
            info = ninArrangeMapper.getInfo(classIdList, teachClassIdList, null);
        }

        //Map<教学班id, List<NinTeachClass>>
        List<NinTeachClass> ninTeachClasses = ninTeachClassMapper.selectList(new QueryWrapper<>());
        Map<Long, List<NinTeachClass>> collect = ninTeachClasses.stream().collect(Collectors.groupingBy(NinTeachClass::getTeachClassId));
        //Map<班级id, 班级名称>
        List<NinClass> ninClasses = ninClassMapper.selectList(new QueryWrapper<>());
        Map<Long, String> collect1 = ninClasses.stream().collect(Collectors.toMap(NinClass::getId, NinClass::getClassName));
        //存放最终结果
        HashMap<String, String> hashMap = new HashMap<>();
        for (Map<String, Object> map : info) {
            if (map.get("classId") == null) {
                List<NinTeachClass> teachClassList = collect.get(map.get("teachClassId"));
                for (NinTeachClass ntc : teachClassList) {
                    if (map.get("className") == null) {
                        map.put("className", collect1.get(ntc.getClassId()));
                    } else {
                        map.put("className", map.get("className") + ";" + collect1.get(ntc.getClassId()));
                    }
                }
            } else {
                map.put("className", collect1.get(map.get("classId")));
            }
        }


        if (count != null) {
            for (Map<String, Object> map : info) {
                //单双周
                if (count % 2 == 0 && (Integer) map.get("weekly") == 1) {
                    continue;
                }
                if (count % 2 == 1 && (Integer) map.get("weekly") == 2) {
                    continue;
                }
                //开始结束范围
                if ((Integer) map.get("endTime") < count) {
                    continue;
                }
                if ((Integer) map.get("startTime") > count) {
                    continue;
                }

                String key = "" + map.get("week") + map.get("pitchNum");

                String value = "" + map.get("courseName") + "/" + map.get("houseName") + "/" + map.get("teacherName") + "/" + map.get("className");

                hashMap.put(key, value);
            }
        } else {
            for (Map<String, Object> map : info) {

                if (map.get("weekly") == null) {
                    continue;
                }

                String key = "" + map.get("week") + map.get("pitchNum");

                String str = map.get("startTime") + "-" + map.get("endTime") + "周";
                if ((Integer) map.get("weekly") == 1) {
                    str = str + "(单)";
                } else if ((Integer) map.get("weekly") == 2) {
                    str = str + "(双)";
                }

                String value = "" + map.get("courseName") + "/" + str + "/" + map.get("houseName") + "/" + map.get("teacherName") + "/" + map.get("className") + "/" + ((int) map.get("must") == 1 ? "必修" : "选修");

                if (hashMap.get(key) == null){
                    hashMap.put(key, value);
                } else {
                    hashMap.put(key, hashMap.get(key) + "//" + value);
                }
            }
        }
        return hashMap;
    }

    /**
     * maxNum分成minNum份，使每份之间的差最小
     *
     * @param maxNum 例：8
     * @param minNum 例：3
     * @return 数组，存放每份的数 例：{3,3,2}
     */
    public int[] grouping(int maxNum, int minNum) {
        int[] ints = new int[minNum];
        for (int i = 0; i < maxNum; i++) {
            ints[i % ints.length]++;
        }
        return ints;
    }

    /**
     * 判断准备放进去的排课是否可以放入
     *
     * @param ninArrangeList           存放最终排课记录的列表
     * @param ninArrange               准备放进去的排课记录
     * @param longListNinTeachClassMap Map<教学班id,List<教学班列表>>
     * @return 可以返回true, 不行则返回false
     */
    public Boolean compare(List<NinArrange> ninArrangeList, NinArrange ninArrange, Map<Long, List<NinTeachClass>> longListNinTeachClassMap) {
        NinArrange arrange1 = ninArrangeList.get(ninArrangeList.size() - 1);
        //和上一条记录相比，同教学班，同课程时，且在同一天，直接跳出
        if (arrange1.getCourseId() == ninArrange.getCourseId() && arrange1.getTeachClassId() == ninArrange.getTeachClassId()) {
            if (arrange1.getWeek() == ninArrange.getWeek()) {
                return false;
            }
        }
        for (NinArrange arrange : ninArrangeList) {
            //为空，表示该记录没有排
            if (arrange.getWeekly() == null) {
                continue;
            }
            //星期节数重叠
            if (ninArrange.getWeek() == arrange.getWeek() && ninArrange.getPitchNum() == arrange.getPitchNum()) {
                //开始结束时间有重叠
                if (!(ninArrange.getStartTime() > arrange.getEndTime() || ninArrange.getEndTime() < arrange.getStartTime())) {
                    //单双周重叠
                    if (ninArrange.getWeekly() == arrange.getWeekly()) {
                        //当时间一样时，开始对比其他条件

                        //教师相同
                        if (ninArrange.getTeacherId() == arrange.getTeacherId()) {
                            return false;
                        }
                        //不是网课或课外(网课id和类型编号均为3，课外id和类型编号为4)
                        if (ninArrange.getHouseId() != 3 && ninArrange.getHouseId() != 4) {
                            //教室相同
                            if (ninArrange.getHouseId() == arrange.getHouseId()) {
                                return false;
                            }
                        }
                        //班级重叠
                        List<Long> A = new ArrayList<>();
                        List<Long> B = new ArrayList<>();
                        //班级id为空，即是教学班，生成存放该教学班包含班级的列表
                        if (arrange.getClassId() == null) {
                            List<NinTeachClass> ninTeachClasses = longListNinTeachClassMap.get(arrange.getTeachClassId());
                            for (NinTeachClass ninTeachClass : ninTeachClasses) {
                                A.add(ninTeachClass.getClassId());
                            }
                        } else {
                            //表示教学班，也将班级单独放进列表
                            A.add(arrange.getClassId());
                        }
                        if (ninArrange.getClassId() == null) {
                            List<NinTeachClass> ninTeachClasses = longListNinTeachClassMap.get(ninArrange.getTeachClassId());
                            for (NinTeachClass ninTeachClass : ninTeachClasses) {
                                B.add(ninTeachClass.getClassId());
                            }
                        } else {
                            B.add(ninArrange.getClassId());
                        }

                        //遍历两个列表，只要有一个相同，即表示发生了冲突
                        for (Long l1 : A) {
                            for (Long l2 : B) {
                                if (l1 == l2) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

}

