package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.bo.NinArrangeBo;
import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.util.ExcelUtils;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import cn.netinnet.coursearrange.util.Utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

        //必修的排课列表
        ArrayList<NinArrange> ninArrangeArrayList = new ArrayList<>();

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
                        long id = IDUtil.getID();
                        for (int k = 0; k < ints[j]; k++, count++) {
                            NinTeachClass ninTeachClass = new NinTeachClass();
                            ninTeachClass.setTeachClassId(id);
                            ninTeachClass.setClassId(ninClasses.get(count).getId());
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
                    ninArrange.setPeopleNum(longListNinTeachClassMap.get(longs[i]).size() * ApplicationConstant.CLASS_PEOPLE_NUM);
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
                throw new ServiceException(412, ninCourse.getCourseName() + "还未有教师");
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
            if (ninArranges1 == null) {
                continue;
            }

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
                            arrange1.setPitchNum((int) (Math.random() * ApplicationConstant.DAY_PITCH_NUM) + 1);
                            if (count++ > 100) {
                                break;
                            }

                        } while (!compare(ninArrangeList, arrange1, longListNinTeachClassMap));
                        if (count > 100) {
                            boolean b = false;
                            ok:
                            for (int j = 0; j < 7; j++) {
                                for (int k = 0; k < ApplicationConstant.DAY_PITCH_NUM; k++) {
                                    arrange1.setWeek(j + 1);
                                    arrange1.setPitchNum(k + 1);
                                    if (compare(ninArrangeList, arrange1, longListNinTeachClassMap)) {
                                        b = true;
                                        break ok;
                                    }
                                }
                            }
                            //如果循环结束。。置空
                            if (!b) {
                                arrange1.setWeekly(null);
                                arrange1.setWeek(null);
                                arrange1.setPitchNum(null);
                                arrange1.setStartTime(null);
                                arrange1.setEndTime(null);
                            }

                        }
                        //id
                        arrange1.setId(IDUtil.getID());
                        if (arrange1.getWeekly() == 1) {
                            arrange1.setEndTime(arrange1.getEndTime() - 1);
                        }
                        if (arrange1.getWeekly() == 2) {
                            arrange1.setStartTime(arrange1.getStartTime() + 1);
                        }
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
                            for (int k = 0; k < ApplicationConstant.DAY_PITCH_NUM; k++) {
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
                            for (int k = 0; k < ApplicationConstant.DAY_PITCH_NUM; k++) {
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
                    if (arrange1.getWeekly() == 1) {
                        arrange1.setEndTime(arrange1.getEndTime() - 1);
                    }
                    if (arrange1.getWeekly() == 2) {
                        arrange1.setStartTime(arrange1.getStartTime() + 1);
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
    public void empty() {
        ninArrangeMapper.empty();
        ninTeachClassMapper.delete(new QueryWrapper<>());
    }

    @Override
    public Map<String, String> getInfo(Long classId, Long teacherId, Long studentId, Integer count) {
        List<NinArrangeBo> info = new ArrayList<>();

        //根据不同的id获取排课记录信息
        if (teacherId != null) {
            //教师
            info = ninArrangeMapper.getInfo(null, null, teacherId);
        } else if (classId != null) {
            //如果是选修班级
            List<Long> longs = new ArrayList<>();
            longs.add(classId);
            info = ninArrangeMapper.getInfo(longs, null, null);
            if (info.size() == 0) {
                //查询如果为空，则为必修班级
                longs.remove(0);
                //根据班级获取教学班id列表
                longs = ninTeachClassMapper.getTeachClassIdList(classId);
                info = ninArrangeMapper.getInfo(null, longs, null);
            }
        } else if (studentId != null) {
            //学生
            //获取学生选课记录
            List<Long> classIdList = new ArrayList<>();
            List<NinStudentCourse> ninStudentCourses = ninStudentCourseMapper.selectList(new QueryWrapper<>(new NinStudentCourse() {{
                setStudentId(getStudentId());
            }}));
            //选课记录不为空，获取其选课班级id
            if (ninStudentCourses != null && ninStudentCourses.size() != 0) {
                for (NinStudentCourse nsc : ninStudentCourses) {
                    classIdList.add(nsc.getTakeClassId());
                }
            }
            //获取学生的行政班id
            NinStudent ninStudent = ninStudentMapper.selectById(studentId);
            //根据行政班id获取教学班id列表
            List<Long> teachClassIdList = ninTeachClassMapper.getTeachClassIdList(ninStudent.getClassId());
            if (classIdList.size() == 0) {
                classIdList = null;
            }
            if (teachClassIdList.size() == 0) {
                teachClassIdList = null;
            }
            info = ninArrangeMapper.getInfo(classIdList, teachClassIdList, null);
        }

        //存放最终结果  星期一第二节（"12"） -> 信息字符串
        HashMap<String, String> hashMap = new HashMap<>();

        //count 查询某一周的课表 空则表示整个学期
        if (count != null) {
            for (NinArrangeBo bo : info) {
                //单双周
                if (count % 2 == 0 && bo.getWeekly() == 1) {//课程记录为单周，但count为双，跳过
                    continue;
                }
                if (count % 2 == 1 && bo.getWeekly() == 2) {//课程记录为双周，但count为单，跳过
                    continue;
                }
                //count在开始结束范围内
                if ((Integer) bo.getEndTime() < count) {
                    continue;
                }
                if ((Integer) bo.getStartTime() > count) {
                    continue;
                }

                String key = "" + bo.getWeek() + bo.getPitchNum();

                String value = "" + bo.getCourseName() + "/" + bo.getHouseName() + "/" + bo.getTeacherName() + "/" + bo.getClassName();

                hashMap.put(key, value);
            }
        } else {
            for (NinArrangeBo bo : info) {

                if (bo.getWeekly() == null) {//为空，即没有时间，跳过
                    continue;
                }

                String key = "" + bo.getWeek() + bo.getPitchNum();

                String str = bo.getStartTime() + "-" + bo.getEndTime() + "周";
                if (bo.getWeek() == 1) {
                    str = str + "(单)";
                } else if (bo.getWeek() == 2) {
                    str = str + "(双)";
                }

                String value = "" + bo.getCourseName() + "/" + str + "/" + bo.getHouseName() + "/" + bo.getTeacherName() + "/" + bo.getClassName() + "/" + (bo.getMust() == 1 ? "必修" : "选修");

                if (bo.getCareerId() == -1) {
                    value += "(补课)";
                }

                //同一节要上两种课(选修和必修或单双周)
                if (hashMap.get(key) == null) {
                    hashMap.put(key, value);
                } else {
                    hashMap.put(key, hashMap.get(key) + "##" + value);
                }
            }
        }
        return hashMap;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getLeisure(Long teacherId, String classIds, Long houseId, Integer houseType, Integer seatMin, Integer seatMax, Integer weekly) {
        List<Map<String, Object>> ninHouseArrayList = new ArrayList<>();

        //如果无限制座位，那么以班级人数上限为准
        if (seatMax == null && seatMin == null) {
            if (!StringUtils.isBlank(classIds)) {
                List<Long> classIdList = JSON.parseArray(classIds, Long.class);
                seatMin = classIdList.size() * ApplicationConstant.CLASS_PEOPLE_NUM;
            }
        }
        //根据座位，类型查询教室
        ninHouseArrayList = ninHouseMapper.getSelectList(null, houseType, seatMin, seatMax);
        Utils.conversion(ninHouseArrayList);

        //如果没有符合条件的教室
        if (ninHouseArrayList != null && ninHouseArrayList.size() != 0) {
        } else {
            throw new ServiceException(412, "无符合条件的教室");
        }

        //获取符合条件的教室列表之后
        //获得排课列表
        List<NinArrange> ninArranges = ninArrangeMapper.selectList(new QueryWrapper<>());

        //当weekly为单数时，去除双周的记录，保留每周都上课和单周上课的记录
        Integer w;
        if (weekly % 2 == 1) {
            w = 2;
        } else {
            w = 1;
        }
        //筛选，在开始结束范围内
        List<NinArrange> ninArrangeList = ninArranges.stream().filter(i -> i.getWeekly() != w).filter(i -> i.getStartTime() <= weekly).filter(i -> i.getEndTime() >= weekly).collect(Collectors.toList());

        //把这个时间的排课找出，如果有教师和班级，排课表中存在，则去除这个时间
        //去除这个时间有使用的教室

        //<星期节数, List<教室Map>>
        Map<String, List<Map<String, Object>>> hashMap = new HashMap<>();

        //遍历时间
        for (int i = 1; i <= 7; i++) {
            ok:
            for (int j = 1; j <= ApplicationConstant.DAY_PITCH_NUM; j++) {

                ArrayList<Map<String, Object>> houseList = new ArrayList<>();
                houseList.addAll(ninHouseArrayList);

                List<Long> teachClassIdList = null;
                //判断教师和班级
                if (!StringUtils.isBlank(classIds)) {
                    List<Long> classIdList = JSON.parseArray(classIds, Long.class);
                    //获取教学班列表
                    teachClassIdList = ninTeachClassMapper.getBatchTeachClassIdList(classIdList);
                }

                for (NinArrange arrange : ninArrangeList) {
                    //一样的时间里
                    if (arrange.getWeek() == i && arrange.getPitchNum() == j) {

                        if (teachClassIdList != null) {
                            //输入条件有班级，且排课存在班级的
                            if (teachClassIdList.contains(arrange.getTeachClassId())) {
                                //那么该时间就不能使用
                                continue ok;

                            }
                        }

                        if (teacherId != null) {
                            //输入条件有教师，且排课中有教师
                            if (teacherId.equals(arrange.getTeacherId())) {
                                //该时间不能使用
                                continue ok;

                            }
                        }

                        //删除houseList里面出现的
                        for (int k = 0; k < houseList.size(); k++) {
                            if (houseList.get(k) != null && houseList.get(k).get("id").equals(String.valueOf(arrange.getHouseId()))) {
                                houseList.set(k, null);
                            }
                        }
                    }
                }

                houseList.removeIf(Objects::isNull);
                if (houseList.size() != 0 && houseList != null) {
                    hashMap.put("" + i + j, houseList);
                }
            }
        }
        return hashMap;
    }

    @Override
    public int addArrange(Integer weekly, Integer week, Integer pitchNum, Long houseId, Long teacherId, Long courseId, String classIdList) {
        List<Long> classIds = JSON.parseArray(classIdList, Long.class);
        if (classIds == null || classIds.size() == 0) {
            throw new ServiceException(412, "班级为空");
        }

        //判断
        Integer arrangeVerify = ninArrangeMapper.getArrangeVerify(classIds, weekly, week, pitchNum, teacherId, houseId);
        if (arrangeVerify > 0) {
            throw new ServiceException(412, "时间冲突，添加失败");
        }

        //添加教学班记录
        Long teachClassId = IDUtil.getID();
        ArrayList<NinTeachClass> ninTeachClasses = new ArrayList<>();
        for (Long classId : classIds) {
            NinTeachClass ninTeachClass = new NinTeachClass();
            ninTeachClass.setTeachClassId(teachClassId);
            ninTeachClass.setClassId(classId);
            ninTeachClass.setModifyUserId(UserUtil.getUserInfo().getUserId());
            ninTeachClass.setCreateUserId(UserUtil.getUserInfo().getUserId());
            ninTeachClasses.add(ninTeachClass);
        }
        ninTeachClassMapper.addBatch(ninTeachClasses);

        //添加排课记录
        NinArrange arrange = new NinArrange();
        arrange.setId(IDUtil.getID());
        arrange.setCareerId(-1L);
        arrange.setTeachClassId(teachClassId);
        arrange.setTeacherId(teacherId);
        arrange.setHouseId(houseId);
        arrange.setCourseId(courseId);
        arrange.setWeekly(0);
        arrange.setWeek(week);
        arrange.setPitchNum(pitchNum);
        arrange.setStartTime(weekly);
        arrange.setEndTime(weekly);
        arrange.setPeopleNum(classIds.size() * ApplicationConstant.CLASS_PEOPLE_NUM);
        arrange.setMust(ninCourseMapper.selectById(courseId).getMust());
        arrange.setModifyUserId(UserUtil.getUserInfo().getUserId());
        arrange.setCreateUserId(UserUtil.getUserInfo().getUserId());
        return ninArrangeMapper.insert(arrange);
    }

    @Override
    public Map<String, Object> getPageSelectList(NinArrangeBo bo, Integer page, Integer size) {
        //如果查询条件有班级
        if (bo.getClassId() != null) {
            NinClass ninClass = ninClassMapper.selectById(bo.getClassId());
            if (ninClass.getCareerId() != 0) {
                //如果不是选修，查询教学班id列表
                //如果是，则什么都不做
                List<Long> teachClassIdList = ninTeachClassMapper.selectList(new QueryWrapper<>(new NinTeachClass() {{
                    setClassId(ninClass.getId());
                }})).stream().map(NinTeachClass::getTeachClassId).collect(Collectors.toList());
                //添加教学班列表，将班级置空
                bo.setTeachClassIdList(teachClassIdList);
                bo.setClassId(null);
            }
        }

        PageHelper.startPage(page, size);
        List<Map<String, Object>> list = ninArrangeMapper.getSelectList(bo);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        Utils.conversion(pageInfo.getList());

        HashMap<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return map;
    }

    @Override
    public int delArrange(Long id) {
        NinArrange arrange = ninArrangeMapper.selectById(id);
        if (arrange.getMust() == 0) {
            //选修
            Long classId = arrange.getClassId();
            Long courseId = arrange.getCourseId();
            //删除班级
            ninClassMapper.deleteById(classId);
            //删除课程
            ninCourseMapper.deleteById(courseId);
            //删除学生选课记录
            ninStudentCourseMapper.delete(new QueryWrapper<>(new NinStudentCourse() {{
                setTakeClassId(classId);
            }}));
            //删除教师选课记录
            ninTeacherCourseMapper.delete(new QueryWrapper<>(new NinTeacherCourse() {{
                setCourseId(courseId);
            }}));
            //删除设置记录
            ninSettingMapper.delete(new QueryWrapper<>(new NinSetting() {{
                setCourseId(courseId);
            }}));
        }
        return ninArrangeMapper.deleteById(id);
    }

    @Override
    public int alterArrange(NinArrange arrange) {
        arrange.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninArrangeMapper.updateById(arrange);
    }

    @Override
    public List<Object> getTeacherHouseORTime(Long courseId, Long teacherId, Long houseId) {

        List<Object> list = new ArrayList<>();

        if (teacherId == null && houseId == null) {
            //返回教师列表和教室列表
            if (courseId == null) {
                throw new ServiceException(412, "课程id不为空");
            }
            List<NinTeacherCourse> ninTeacherCourses = ninTeacherCourseMapper.selectList(new QueryWrapper<>(new NinTeacherCourse() {{
                setCourseId(courseId);
            }}));
            if (ninTeacherCourses == null || ninTeacherCourses.size() == 0) {
                throw new ServiceException(412, "没有教师选择该课程");
            }
            Map<Long, String> teacherMap = ninTeacherMapper.selectList(new QueryWrapper<>()).stream().collect(Collectors.toMap(NinTeacher::getId, NinTeacher::getTeacherName));

            //教师列表
            List<Map<String, Object>> teacherList = new ArrayList<>();
            for (NinTeacherCourse ntc : ninTeacherCourses) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("teacherId", ntc.getTeacherId());
                hashMap.put("teacherName", teacherMap.get(ntc.getTeacherId()));
                teacherList.add(hashMap);
            }

            List<NinHouse> ninHouses = ninHouseMapper.selectList(new QueryWrapper<>(new NinHouse() {{
                setHouseType(ninCourseMapper.selectById(courseId).getHouseType());
            }}));
            List<Map<String, Object>> houseList = ninHouses.stream().map(i -> {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("houseId", i.getId());
                hashMap.put("houseName", i.getHouseName());
                return hashMap;
            }).collect(Collectors.toList());

            list.add(teacherList);
            list.add(houseList);

        }
        if (teacherId != null && houseId != null) {
            //返回时间列表
            List<NinArrange> ninArranges = ninArrangeMapper.selectList(new QueryWrapper<NinArrange>().eq("teacher_id", teacherId).or().eq("house_id", houseId));
            List<String> timeList = ninArranges.stream().map(i -> {
                String str = "" + i.getWeek() + i.getPitchNum();
                return str;
            }).collect(Collectors.toList());
            for (int i = 1; i <= 7; i++) {
                ok:
                for (int j = 1; j <= ApplicationConstant.DAY_PITCH_NUM; j++) {
                    for (String s : timeList) {
                        if (s.equals("" + i + j)) {
                            continue ok;
                        }
                    }
                    list.add("" + i + j);
                }
            }

        }
        if (teacherId == null && houseId != null) {
            throw new ServiceException(412, "请选择教师");
        }
        if (teacherId != null && houseId == null) {
            throw new ServiceException(412, "请选择教室");
        }

        return list;
    }

    @Override
    public void exportCourseForm(String type, Long id, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> info = null;
        String name = null;
        //根据type和id获取排课记录列表和id代表的角色名称
        if (type.equals("class")) {
            name = ninClassMapper.selectById(id).getClassName();
            info = getInfo(id, null, null, null);
        } else if (type.equals("teacher")) {
            name  = ninTeacherMapper.selectById(id).getTeacherName();
            info = getInfo(null, id, null, null);
        } else if (type.equals("student")) {
            name = ninStudentMapper.selectById(id).getStudentName();
            info = getInfo(null, null, id, null);
        }
        int len = 8;
        //表头名称，值代表的变量名
        String[] headers = new String[len], fields = new String[len];

        for (int i = 0; i < 8; i++) {
            headers[i] = Utils.cnWeek(i);//{"", "星期一", "星期二", ..}
            fields[i] = "" + i;//{"pitchNum", "1", "2", ..}
        }
        fields[0] = "pitchNum";


        //获得并拼接数据
        JSONArray array = new JSONArray();
        for (int i = 0; i < ApplicationConstant.DAY_PITCH_NUM; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pitchNum", Utils.cnPitchNum(i + 1));
            array.add(jsonObject);
        }
        for (Map.Entry<String, String> map: info.entrySet()) {
            String[] split = map.getKey().split("");
            int i = Integer.valueOf(split[0]).intValue();
            int j = Integer.valueOf(split[1]).intValue();
            ((JSONObject) array.get(j - 1)).put("" + i, map.getValue());
        }

        String fileName = name + "-课程表.xls";
        response.setContentType("application/octet-stream");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        try {
            response.setHeader("Content-Disposition", URLEncoder.encode(fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            response.flushBuffer();
            ExcelUtils.exportJsonArrayToExcel(array, fileName, response.getOutputStream(), headers, fields, "");
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        if (ninArrangeList.size() > 1) {
            NinArrange arrange1 = ninArrangeList.get(ninArrangeList.size() - 1);
            //和上一条记录相比，同教学班，同课程时，且在同一天，直接跳出
            if (arrange1.getCourseId().equals(ninArrange.getCourseId()) && arrange1.getTeachClassId().equals(ninArrange.getTeachClassId())) {
                if (arrange1.getWeek() == ninArrange.getWeek()) {
                    return false;
                }
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
                    if (ninArrange.getWeekly() == 0 || arrange.getWeekly() == 0 || ninArrange.getWeekly() == arrange.getWeekly()) {
                        //当时间一样时，开始对比其他条件

                        //教师相同
                        if (ninArrange.getTeacherId().equals(arrange.getTeacherId())) {
                            return false;
                        }
                        //不是网课或课外(网课id和类型编号均为3，课外id和类型编号为4)
                        if (ninArrange.getHouseId() != 3 && ninArrange.getHouseId() != 4) {
                            //教室相同
                            if (ninArrange.getHouseId().equals(arrange.getHouseId())) {
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
                                if (l1.equals(l2)) {
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

