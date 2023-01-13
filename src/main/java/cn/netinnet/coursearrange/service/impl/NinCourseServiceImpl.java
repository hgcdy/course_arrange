package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.bo.CourseBo;
import cn.netinnet.coursearrange.bo.SettingBo;
import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinCourseService;
import cn.netinnet.coursearrange.service.INinSettingService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import cn.netinnet.coursearrange.util.CnUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
public class NinCourseServiceImpl extends ServiceImpl<NinCourseMapper, NinCourse> implements INinCourseService {

    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinHouseMapper ninHouseMapper;
    @Autowired
    private NinArrangeMapper ninArrangeMapper;
    @Autowired
    private NinSettingMapper ninSettingMapper;
    @Autowired
    private INinSettingService ninSettingService;
    @Autowired
    private NinStudentCourseMapper ninStudentCourseMapper;
    @Autowired
    private NinTeacherCourseMapper ninTeacherCourseMapper;
    @Autowired
    private NinCareerCourseMapper ninCareerCourseMapper;

    @Override
    public Map<String, Object> getPageSelectList(Integer page, Integer size, NinCourse ninCourse) {
        PageHelper.startPage(page, size);
        List<CourseBo> list = ninCourseMapper.getSelectList(ninCourse);
        PageInfo<CourseBo> pageInfo = new PageInfo<>(list);

        pageInfo.getList().stream().forEach(i -> {
            if (i.getMust() != null) {
                i.setCnMust(CnUtil.cnMust(i.getMust()));
            }
            if (i.getHouseType() != null) {
                i.setCnHouseType(CnUtil.cnHouse(i.getHouseType()));
            }
        });

        HashMap<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return map;
    }

    @Override
    public NinCourse getCourseById(Long id) {
        return ninCourseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addSingle(NinCourse ninCourse) {

        //同名验证
        Integer integer = ninCourseMapper.selectCount(
                new QueryWrapper<NinCourse>()
                        .eq("course_name", ninCourse.getCourseName()));
        if (integer > 0) {
            throw new ServiceException(412, "重名");
        }

        if (ninCourse.getHouseType() != 3 && ninCourse.getHouseType() != 4) {
            List<NinHouse> houses = ninHouseMapper.selectList(new QueryWrapper<>(new NinHouse() {{
                setHouseType(ninCourse.getHouseType());
            }})).stream().sorted(Comparator.comparing(NinHouse::getSeat).reversed()).collect(Collectors.toList());
            if (houses != null && houses.size() != 0) {
                List<Integer> list = houses.stream().map(NinHouse::getSeat).filter(i -> i > ninCourse.getMaxClassNum() * 50).collect(Collectors.toList());
                if (list.size() == 0) {
                    throw new ServiceException(412, "没有可容纳" + ninCourse.getMaxClassNum() + "个班级一起上课的教室");
                }
            } else {
                throw new ServiceException(412, "没有可容纳" + ninCourse.getMaxClassNum() + "个班级一起上课的教室");
            }
        }

        Long userId = UserUtil.getUserInfo().getUserId();

        ninCourse.setId(IDUtil.getID());
        ninCourse.setCreateUserId(userId);
        ninCourse.setModifyUserId(userId);
        int i = ninCourseMapper.insert(ninCourse);

        //教师权限记录
        NinSetting ninSetting = new NinSetting();
        ninSetting.setId(IDUtil.getID());
        ninSetting.setCourseId(ninCourse.getId());
        ninSetting.setUserType(UserTypeEnum.TEACHER.getName());
        ninSetting.setOpenState(0);
        ninSetting.setCreateUserId(userId);
        ninSetting.setModifyUserId(userId);
        ninSettingMapper.insert(ninSetting);

        //如果是选修课程
        if (ninCourse.getMust() == 0) {
            //生成选修教学班
            NinClass ninClass = new NinClass();
            ninClass.setId(IDUtil.getID());
            ninClass.setCareerId(0L);
            ninClass.setClassName(ninCourse.getCourseName() + "选修班");
            ninClass.setCourseNum(1);
            ninClass.setCreateUserId(userId);
            ninClass.setModifyUserId(userId);
            ninClassMapper.insert(ninClass);

            //生成Arrange
            NinArrange arrange = new NinArrange();
            arrange.setId(IDUtil.getID());
            arrange.setCareerId(0L);
            arrange.setClassId(ninClass.getId());
            arrange.setCourseId(ninCourse.getId());
            arrange.setMust(0);
            arrange.setWeekly(0);
            arrange.setStartTime(ninCourse.getStartTime() != null ? ninCourse.getStartTime() : 1);
            arrange.setEndTime(ninCourse.getEndTime() != null ? ninCourse.getEndTime() : 16);
            arrange.setPeopleNum(0);
            arrange.setModifyUserId(userId);
            arrange.setCreateUserId(userId);
            ninArrangeMapper.insert(arrange);

            //生成学生权限记录
            ninSetting.setId(IDUtil.getID());
            ninSetting.setUserType(UserTypeEnum.STUDENT.getName());
            ninSettingMapper.insert(ninSetting);
        }
        return i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delById(Long id) {
        NinCourse course = ninCourseMapper.selectById(id);
        //删除其他表有关该课程的记录

        //删除教师-课程
        ninTeacherCourseMapper.delete(new QueryWrapper<>(new NinTeacherCourse() {{
            setCourseId(id);
        }}));
        //删除选课权限表
        ninSettingMapper.delete(new QueryWrapper<>(new NinSetting() {{
            setCourseId(id);
        }}));

        if (course.getMust() == 0) {
            //删除学生-课程
            ninStudentCourseMapper.delete(new QueryWrapper<>(new NinStudentCourse() {{
                setCourseId(id);
            }}));
            //课程id查询排课信息
            NinArrange arrange = ninArrangeMapper.selectOne(new QueryWrapper<>(new NinArrange() {{
                setCourseId(id);
            }}));
            //获取班级id
            Long classId = arrange.getClassId();
            //删除选修班级
            ninClassMapper.deleteById(classId);

        } else {

            //获取有选择该课程的专业列表
            List<NinCareerCourse> ninCareerCourses = ninCareerCourseMapper.selectList(new QueryWrapper<>(new NinCareerCourse() {{
                setCourseId(id);
            }}));
            if (ninCareerCourses != null && ninCareerCourses.size() != 0) {
                //获取专业id并去重
                List<Long> careerIdList = ninCareerCourses.stream().map(NinCareerCourse::getCareerId).distinct().collect(Collectors.toList());

                ArrayList<Map<String, Object>> maps = new ArrayList<>();
                for (Long careerId : careerIdList) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("careerId", careerId);
                    hashMap.put("courseNum", -1);
                    maps.add(hashMap);
                }
                //班级课程数-1
                ninClassMapper.alterBatchCourseNum(maps);
                //删除专业-课程表
                ninCareerCourseMapper.delete(new QueryWrapper<>(new NinCareerCourse() {{
                    setCourseId(id);
                }}));
            }
        }
        //删除课程
        int i = ninCourseMapper.deleteById(id);
        return i;
    }

    @Override
    public int alterSingle(NinCourse ninCourse) {
        //同名验证
        Integer i = ninCourseMapper.selectCount(
                new QueryWrapper<NinCourse>()
                        .eq("course_name", ninCourse.getCourseName())
                        .ne("id", ninCourse.getId()));
        if (i > 0) {
            throw new ServiceException(412, "重名");
        }

        ninCourse.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninCourseMapper.updateById(ninCourse);
    }

    @Override
    public List<NinCourse> getSelectCourseList(Integer sign) {
        UserInfo userInfo = UserUtil.getUserInfo();
        String userType = userInfo.getUserType();

        List<NinCourse> courseList = ninCourseMapper.selectList(new QueryWrapper<>());
        //0-返回选修课程，1-返回必修课程
        if (sign != null && (sign == 0 || sign == 1)) {
            courseList = courseList.stream().filter(i ->
                    i.getMust() == sign
            ).collect(Collectors.toList());
        } else {
            //如果不是0或1，即表示是教师获取可选的课程（返回除被选的选修课程外的所有课程）
            courseList = ninCourseMapper.reSelectCourse();
        }

        if (!userType.equals(UserTypeEnum.ADMIN.getName())) {
            Map<Long, SettingBo> boMap = ninSettingService.getSelectList(userType, 1, null).stream().collect(Collectors.toMap(SettingBo::getCourseId, Function.identity()));
            courseList = courseList.stream().filter(i -> boMap.get(i.getId()) != null).collect(Collectors.toList());

        }
        return courseList;

    }

    @Override
    public List<NinCourse> getSelectApplyList(Long teacherId, Long houseId, String classIdList) {
        List<Long> classIds = JSON.parseArray(classIdList, Long.class);
        if (classIds == null || classIds.size() == 0) {
            throw new ServiceException(412, "班级为空！");
        }
        //教师的课程列表
        List<NinTeacherCourse> ninTeacherCourses = ninTeacherCourseMapper.selectList(new QueryWrapper<>(new NinTeacherCourse() {{
            setTeacherId(teacherId);
        }}));
        if (ninTeacherCourses == null || ninTeacherCourses.size() == 0) {
            throw new ServiceException(412, "该教师暂无可授课课程");
        }
        List<Long> courseIdList = ninTeacherCourses.stream().map(NinTeacherCourse::getCourseId).collect(Collectors.toList());

        //获取班级列表<classId, careerId>
        Map<Long, Long> classCareerMap = ninClassMapper.selectList(new QueryWrapper<>()).stream().collect(Collectors.toMap(NinClass::getId, NinClass::getCareerId));

        //根据班级id获取专业id列表
        HashSet<Long> careerIdSet = new HashSet<>();
        for (Long l : classIds) {
            careerIdSet.add(classCareerMap.get(l));
        }

        //专业选课表<careerId, List<NinCareerCourse>>
        Map<Long, List<NinCareerCourse>> careerIdMap = ninCareerCourseMapper.selectList(new QueryWrapper<>()).stream().collect(Collectors.groupingBy(NinCareerCourse::getCareerId));


        HashSet<Long> courseIdSet = new HashSet<>();
        //将每个专业的选课放进去，计数，如果课程id出现次数等于专业数量，即这几个专业中重复的课程
        ArrayList<Long> courseIds = new ArrayList<>();
        for (Long l : careerIdSet) {
            for (NinCareerCourse ninCareerCourse : careerIdMap.get(l)) {
                courseIds.add(ninCareerCourse.getCourseId());
            }
        }
        //计数
        Map<Long, Long> collect = courseIds.stream().collect(Collectors.groupingBy(it -> it, Collectors.counting()));
        for (Map.Entry<Long, Long> map : collect.entrySet()) {
            if (map.getValue() == careerIdSet.size()) {
                courseIdSet.add(map.getKey());
            }
        }

        if (courseIdSet.size() == 0) {
            throw new ServiceException(412, "所选的班级无共同的课程");
        }

        //选出共有的课程
        courseIdList.addAll(courseIdSet);
        List<Long> longs = new ArrayList<>();
        Map<Long, Long> map = courseIdList.stream().collect(Collectors.groupingBy(i -> i, Collectors.counting()));
        for (Map.Entry<Long, Long> m: map.entrySet()) {
            if (m.getValue() > 1) {
                longs.add(m.getKey());
            }
        }

        if (longs.size() == 0) {
            throw new ServiceException(412, "没有符合条件的课程");
        }

        ArrayList<NinCourse> ninCourses = new ArrayList<>();

        Map<Long, NinCourse> courseIdMap = ninCourseMapper.selectList(new QueryWrapper<>()).stream().collect(Collectors.toMap(NinCourse::getId, Function.identity()));
        Integer houseType = ninHouseMapper.selectById(houseId).getHouseType();
        //如果符合教室的类型，写入列表
        for (Long l : longs) {
            NinCourse course = courseIdMap.get(l);
            if (houseType == course.getHouseType()) {
                ninCourses.add(course);
            }
        }

        if (ninCourses == null || ninCourses.size() == 0) {
            throw new ServiceException(412, "所选教室无可上的课程");
        }

        return ninCourses;
    }
}
