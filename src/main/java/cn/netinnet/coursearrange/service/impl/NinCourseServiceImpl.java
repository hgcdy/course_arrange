package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinCourseService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
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
    private NinStudentCourseMapper ninStudentCourseMapper;
    @Autowired
    private NinClassCourseMapper ninClassCourseMapper;
    @Autowired
    private NinTeacherCourseMapper ninTeacherCourseMapper;
    @Autowired
    private NinCareerCourseMapper ninCareerCourseMapper;

    @Override
    public Map<String, Object> getPageSelectList(Integer page, Integer size, NinCourse ninCourse) {
        PageHelper.startPage(page, size);
        List<NinCourse> list = ninCourseMapper.getSelectList(ninCourse);
        PageInfo<NinCourse> pageInfo = new PageInfo<>(list);
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
    public int addSingle(NinCourse ninCourse) {
        //同名验证
        Integer integer = ninCourseMapper.selectCount(
                new QueryWrapper<NinCourse>()
                        .eq("course_name", ninCourse.getCourseName()));
        if (integer > 0) {
            throw new ServiceException(412, "重名");
        }
        ninCourse.setId(IDUtil.getID());
        ninCourse.setCreateUserId(UserUtil.getUserInfo().getUserId());
        ninCourse.setModifyUserId(UserUtil.getUserInfo().getUserId());
        int i = ninCourseMapper.insert(ninCourse);
        //如果是选修课程
        if (ninCourse.getMust() == 0) {
            //生成选修教学班
            NinClass ninClass = new NinClass();
            ninClass.setId(IDUtil.getID());
            ninClass.setClassName(ninCourse.getCourseName() + "1班");
            ninClass.setCourseNum(1);
            ninClass.setCreateUserId(UserUtil.getUserInfo().getUserId());
            ninClass.setModifyUserId(UserUtil.getUserInfo().getUserId());
            ninClassMapper.insert(ninClass);

            //生成班级-课程表
            NinClassCourse ninClassCourse = new NinClassCourse();
            ninClassCourse.setId(IDUtil.getID());
            ninClassCourse.setCourseId(ninCourse.getId());
            ninClassCourse.setClassId(ninClass.getId());
            ninClassCourse.setCreateUserId(UserUtil.getUserInfo().getUserId());
            ninClassCourse.setModifyUserId(UserUtil.getUserInfo().getUserId());
            ninClassCourseMapper.insert(ninClassCourse);
        }
        return i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delById(Long id) {
        //删除时前端确认
        int i = ninCourseMapper.deleteById(id);
        //删除其他表有关该课程的记录

//        QueryWrapper<NinClassCourse> queryWrapper = new QueryWrapper<>(new NinClassCourse() {{
//            setCourseId(id);
//        }});
//
//        //班级的课程数-1
//        //获取班级id列表
//        List<Long> classIdList = ninClassCourseMapper.selectList(queryWrapper).stream().map(ninClassCourse -> {
//            Long classId = ninClassCourse.getClassId();
//            return classId;
//        }).collect(Collectors.toList());
//
//        //班级课程数num-1
//        ninClassMapper.subBatchCourseNum(classIdList);
//
//        //删除班级-课程
//        ninClassCourseMapper.delete(queryWrapper);

        //删除学生-课程
        ninStudentCourseMapper.delete(new QueryWrapper<>(new NinStudentCourse() {{
            setCourseId(id);
        }}));

        //删除教师-课程
        ninTeacherCourseMapper.delete(new QueryWrapper<>(new NinTeacherCourse() {{
            setCourseId(id);
        }}));
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

        NinCourse ninCourseOld = ninCourseMapper.selectById(ninCourse.getId());

        //修改选修必修属性
        if (ninCourse.getMust() != null && ninCourse.getMust() != ninCourseOld.getMust()) {
            //班级-课程表
            Integer i1 = ninClassCourseMapper.selectCount(
                    new QueryWrapper<>(new NinClassCourse() {{
                        setCourseId(ninCourse.getId());
                    }}));
            if (i1 > 0) {
                throw new ServiceException(412, "已有班级选择必修该课程");
            }
            //选修改必修
            if (ninCourse.getMust() == 1) {
                //学生-课程表
                Integer i2 = ninStudentCourseMapper.selectCount(
                        new QueryWrapper<>(new NinStudentCourse() {{
                            setCourseId(ninCourse.getId());
                        }}));
                if (i2 > 0) {
                    throw new ServiceException(412, "已有学生选修该课程");
                }
            }
        }

        ninCourse.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninCourseMapper.updateById(ninCourse);
    }

    @Override
    public List<NinCourse> getSelectCourseList(Integer sign) {
        List<NinCourse> courseList = ninCourseMapper.selectList(new QueryWrapper<>());
        if (sign != null && (sign == 0 || sign == 1)) {
            courseList = courseList.stream().filter(i ->
                    i.getMust() == sign
            ).collect(Collectors.toList());
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
        if (ninTeacherCourses ==null || ninTeacherCourses.size() == 0) {
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
            for (NinCareerCourse ninCareerCourse: careerIdMap.get(l)) {
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

        //加入教师的课程列表，去重
        courseIdSet.addAll(courseIdList);

        ArrayList<NinCourse> ninCourses = new ArrayList<>();

        Map<Long, NinCourse> courseIdMap = ninCourseMapper.selectList(new QueryWrapper<>()).stream().collect(Collectors.toMap(NinCourse::getId, Function.identity()));
        Integer houseType = ninHouseMapper.selectById(houseId).getHouseType();
        //如果符合教室的类型，写入列表
        for (Long l : courseIdSet) {
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
