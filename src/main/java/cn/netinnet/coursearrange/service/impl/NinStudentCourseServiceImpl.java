package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.enums.CourseTypeEnum;
import cn.netinnet.coursearrange.enums.MsgEnum;
import cn.netinnet.coursearrange.enums.OpenStateEnum;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinClassService;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @Autowired
    private NinSettingMapper ninSettingMapper;
    @Autowired
    private NinMessageMapper ninMessageMapper;
    @Autowired
    private NinHouseMapper ninHouseMapper;


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

        List<NinSetting> ninSettings = ninSettingMapper.selectList(new LambdaQueryWrapper<NinSetting>()
                .select(NinSetting::getCourseId, NinSetting::getOpenState)
                .eq(NinSetting::getOpenState, OpenStateEnum.OPEN.getCode())
                .eq(NinSetting::getUserType, UserTypeEnum.STUDENT.getName()));
        Map<Long, Integer> stateMap = ninSettings.stream().collect(Collectors.toMap(NinSetting::getCourseId, NinSetting::getOpenState));

        courseList.forEach(i -> {
            Long courseId = i.getId();
            Map<String, Object> map = new HashMap<>();
            map.put("id", courseId.toString());
            map.put("name", i.getCourseName());
            if (stateMap.get(courseId) != null) {
                map.put("isOk", true);
            } else {
                map.put("isOk", false);
            }
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
    public boolean addSingle(Long studentId, Long courseId) {

        List<NinStudentCourse> list = list(new LambdaQueryWrapper<NinStudentCourse>().eq(NinStudentCourse::getStudentId, studentId));
        int count = list.size();
        if (count >= ApplicationConstant.STUDENT_COURSE_NUM) {
            throw new ServiceException(412, "选课数量已经达到上限");
        }

        //选择课程对应排课记录
        NinArrange ninArrange = ninArrangeMapper.selectOne(new LambdaQueryWrapper<NinArrange>()
                .select(NinArrange::getWeek, NinArrange::getPitchNum)
                .in(NinArrange::getCourseId, courseId));

        NinHouse house = ninHouseMapper.selectById(ninArrange.getHouseId());
        if (ninArrange.getPeopleNum() >= house.getSeat()) {
            throw new ServiceException(412, "该课程人数已满");
        }

        if (count != 0) {
            List<Long> courseIdList = list.stream().map(NinStudentCourse::getCourseId).collect(Collectors.toList());
            //该学生已选的课程记录并对比时间(如果有的话)
            List<NinArrange> ninArranges = ninArrangeMapper.selectList(new LambdaQueryWrapper<NinArrange>()
                    .select(NinArrange::getWeek, NinArrange::getPitchNum)
                    .in(NinArrange::getCourseId, courseIdList));
            Integer week = ninArrange.getWeek();
            Integer pitchNum = ninArrange.getPitchNum();
            for (NinArrange arrange : ninArranges) {
                if (Objects.equals(arrange.getWeek(), week) && Objects.equals(arrange.getPitchNum(), pitchNum)) {
                    throw new ServiceException(412, "时间冲突，请重新选择");
                }
            }
        }

        //获取班级id
        Long classId = ninArrange.getClassId();

        //排课信息的人数+1
        ninArrange.setPeopleNum(ninArrange.getPeopleNum() + 1);
        ninArrangeMapper.updateById(ninArrange);

        //班级人数+1
        ninClassMapper.addPeopleNum(classId);

        if (UserTypeEnum.ADMIN.getName().equals(UserUtil.getUserInfo().getUserType())) {
            //如果是管理员操作则添加消息
            NinCourse course = ninCourseMapper.selectById(courseId);
            String msg = StringUtils.format(MsgEnum.ADD_COURSE.getMsg(), course.getCourseName());
            ninMessageMapper.insert(new NinMessage(){{
                setUserId(studentId);
                setMsg(msg);
            }});
        }

        return save(new NinStudentCourse(){{
            setStudentId(studentId);
            setCourseId(courseId);
            setTakeClassId(classId);
        }});
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delSingle(Long studentId, Long courseId) {
        //班级人数-1
        NinStudentCourse ninStudentCourse = ninStudentCourseMapper
                .selectOne(new LambdaQueryWrapper<NinStudentCourse>()
                        .eq(NinStudentCourse::getStudentId, studentId)
                        .eq(NinStudentCourse::getCourseId, courseId));
        //班级人数-1
        ninClassMapper.subPeopleNum(ninStudentCourse.getTakeClassId());

        NinArrange arrange = ninArrangeMapper.selectOne(new LambdaQueryWrapper<NinArrange>()
                .eq(NinArrange::getClassId, ninStudentCourse.getTakeClassId()));

        //排课信息的人数-1
        arrange.setPeopleNum(arrange.getPeopleNum() - 1);
        ninArrangeMapper.updateById(arrange);

        if (UserTypeEnum.ADMIN.getName().equals(UserUtil.getUserInfo().getUserType())) {
            //如果是管理员操作则添加消息
            NinCourse course = ninCourseMapper.selectById(courseId);
            String msg = StringUtils.format(MsgEnum.DEL_COURSE.getMsg(), course.getCourseName());
            ninMessageMapper.insert(new NinMessage(){{
                setUserId(studentId);
                setMsg(msg);
            }});
        }

        return removeById(ninStudentCourse.getId());
    }
}
