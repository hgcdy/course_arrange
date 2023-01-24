package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.enums.CourseTypeEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinClassService;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import cn.netinnet.coursearrange.util.CnUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class NinStudentCourseServiceImpl extends ServiceImpl<NinStudentCourseMapper, NinStudentCourse> implements INinStudentCourseService {

    @Autowired
    private NinStudentCourseMapper ninStudentCourseMapper;
    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private NinArrangeMapper ninArrangeMapper;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private INinClassService ninClassService;
    @Autowired
    private NinStudentMapper ninStudentMapper;


    @Override
    public Map<String, List<Map<String, Object>>> getCourse(Long id) {
        NinStudent ninStudent = ninStudentMapper.selectById(id);
        Long classId = ninStudent.getClassId();

        List<NinStudentCourse> ninStudentCourses = list(new LambdaQueryWrapper<NinStudentCourse>()
                .select(NinStudentCourse::getCourseId)
                .eq(NinStudentCourse::getStudentId, id));
        //已选课程
        List<Long> selectedList = ninStudentCourses.stream().map(NinStudentCourse::getCourseId).collect(Collectors.toList());

        List<NinCourse> courseList = ninCourseMapper.selectList(new LambdaQueryWrapper<NinCourse>()
                .select(NinCourse::getId, NinCourse::getCourseName)
                .eq(NinCourse::getMust, CourseTypeEnum.OPTIONAL.getCode()));

        Map<String, List<Map<String, Object>>> result = ninClassService.getCourse(classId);

        courseList.forEach(i -> {
            Long courseId = i.getId();
            Map<String, Object> map = new HashMap<>();
            map.put("id", courseId.toString());
            map.put("name", i.getCourseName());
            map.put("isOk", true);
            if (selectedList.contains(courseId)) {
                result.get("selected").add(map);
            } else {
                result.get("unselected").add(map);
            }
        });
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addSingle(Long studentId, Long courseId) {

        //判断学生是否已经有选修
        List<NinStudentCourse> ninStudentCourses = ninStudentCourseMapper.selectList(new QueryWrapper<>(new NinStudentCourse() {{
            setStudentId(studentId);
        }}));
        if (ninStudentCourses != null && ninStudentCourses.size() != 0){
            if (ninStudentCourses.size() >= ApplicationConstant.STUDENT_COURSE_NUM){
                throw new ServiceException(412, "该学生选修数量已经上限");
            }
            //判断课程是否已经被选修
            List<Long> courseIds = ninStudentCourses.stream()
                    .map(NinStudentCourse::getCourseId).collect(Collectors.toList());

            if (courseIds.contains(courseId)){
                throw new ServiceException(412, "该学生已经选修了这门课程！");
            }
            Map<Long, NinArrange> map = ninArrangeMapper.selectList(new QueryWrapper<>(new NinArrange() {{
                setMust(0);
            }})).stream().collect(Collectors.toMap(NinArrange::getCourseId, Function.identity()));
            NinArrange arrange = map.get(courseId);
            int week = arrange.getWeek();
            int pitchNum = arrange.getPitchNum();
            for (Long cid : courseIds) {
                NinArrange arrange1 = map.get(cid);
                if (arrange1.getWeek() == week && arrange1.getPitchNum() == pitchNum) {
                    throw new ServiceException(412, "时间冲突，请重新选择");
                }
            }

        }

        NinArrange arrange = ninArrangeMapper.selectOne(new QueryWrapper<>(new NinArrange() {{
            setCourseId(courseId);
        }}));

        //获取班级id
        Long classId = arrange.getClassId();

        //排课信息的人数+1
        arrange.setPeopleNum(arrange.getPeopleNum() + 1);
        ninArrangeMapper.updateById(arrange);

        //班级人数+1
        ninClassMapper.addPeopleNum(classId);

        return ninStudentCourseMapper.insert(new NinStudentCourse(){{
            setStudentId(studentId);
            setCourseId(courseId);
            setTakeClassId(classId);
        }});
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delSingle(Long studentId, Long courseId) {
        //班级人数-1
        NinStudentCourse ninStudentCourse = ninStudentCourseMapper
                .selectOne(new LambdaQueryWrapper<NinStudentCourse>()
                        .eq(NinStudentCourse::getStudentId, studentId)
                        .eq(NinStudentCourse::getCourseId, courseId));
        ninClassMapper.subPeopleNum(ninStudentCourse.getTakeClassId());

        NinArrange arrange = ninArrangeMapper.selectOne(new QueryWrapper<>(new NinArrange() {{
            setClassId(ninStudentCourse.getTakeClassId());
        }}));

        //排课信息的人数+1
        arrange.setPeopleNum(arrange.getPeopleNum() + 1);
        ninArrangeMapper.updateById(arrange);

        return ninStudentCourseMapper.deleteById(ninStudentCourse.getId());
    }
}
