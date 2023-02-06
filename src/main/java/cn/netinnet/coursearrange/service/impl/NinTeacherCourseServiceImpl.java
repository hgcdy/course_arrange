package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.enums.MsgEnum;
import cn.netinnet.coursearrange.enums.OpenStateEnum;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinMessageService;
import cn.netinnet.coursearrange.service.INinTeacherCourseService;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@Service
public class NinTeacherCourseServiceImpl extends ServiceImpl<NinTeacherCourseMapper, NinTeacherCourse> implements INinTeacherCourseService {

    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private NinArrangeMapper ninArrangeMapper;
    @Autowired
    private NinSettingMapper ninSettingMapper;
    @Autowired
    private INinMessageService ninMessageService;


    @Override
    public Map<String, List<Map<String, Object>>> getCourse(Long id) {
        List<NinTeacherCourse> list = list(new LambdaQueryWrapper<NinTeacherCourse>()
                .select(NinTeacherCourse::getCourseId)
                .eq(NinTeacherCourse::getTeacherId, id));
        List<Long> selectCourses = list.stream().map(NinTeacherCourse::getCourseId).collect(Collectors.toList());
        List<NinCourse> courseList = ninCourseMapper.selectList(new LambdaQueryWrapper<NinCourse>()
                .select(NinCourse::getId, NinCourse::getCourseName));

        List<Map<String, Object>> selectedList = new ArrayList<>();
        List<Map<String, Object>> unselectedList = new ArrayList<>();

        List<NinSetting> ninSettings = ninSettingMapper.selectList(new LambdaQueryWrapper<NinSetting>()
                .select(NinSetting::getCourseId, NinSetting::getOpenState)
                .eq(NinSetting::getOpenState, OpenStateEnum.OPEN.getCode())
                .eq(NinSetting::getUserType, UserTypeEnum.TEACHER.getName()));
        Map<Long, Integer> stateMap = ninSettings.stream().collect(Collectors.toMap(NinSetting::getCourseId, NinSetting::getOpenState));


        courseList.forEach(i -> {
            Long courseId = i.getId();
            Map<String, Object> map = new HashMap<>();
            map.put("id", courseId.toString());
            map.put("name", i.getCourseName());
            if (null != stateMap.get(courseId)) {
                map.put("isOk", true);
            } else {
                map.put("isOk", false);
            }
            if (selectCourses.contains(courseId)) {
                selectedList.add(map);
            } else {
                unselectedList.add(map);
            }
        });
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("selected", selectedList);
        result.put("unselected", unselectedList);
        return result;
    }

    @Override
    public boolean addSingle(Long teacherId, Long courseId) {
        List<NinTeacherCourse> ninTeacherCourses = list(new LambdaQueryWrapper<NinTeacherCourse>().eq(NinTeacherCourse::getTeacherId, teacherId));
        int count = ninTeacherCourses.size();
        if (count >= ApplicationConstant.TEACHER_COURSE_NUM){
            throw new ServiceException(412, "该教师选课数量已经上限");
        }

        //课程对应记录
        NinArrange arrange = ninArrangeMapper.selectOne(new LambdaQueryWrapper<NinArrange>()
                .eq(NinArrange::getCourseId, courseId));
        arrange.setTeacherId(teacherId);

        NinCourse course = ninCourseMapper.selectById(courseId);
        if (course.getMust() == 0 && count > 0) {
            //如果是选修课，看两个选修课之间是否冲突
            //此刻排课记录中只有选修课
            List<NinArrange> ninArranges = ninArrangeMapper.selectList(new LambdaQueryWrapper<NinArrange>()
                    .eq(NinArrange::getTeacherId, teacherId));

            for (NinArrange ninArrange : ninArranges) {
                if (ninArrange.getWeek().equals(arrange.getWeek()) && ninArrange.getPitchNum().equals(arrange.getPitchNum())) {
                    throw new ServiceException(412, "该课程和其他课程时间发生冲突");
                }
            }
        }

        if (UserTypeEnum.ADMIN.getName().equals(UserUtil.getUserInfo().getUserType())) {
            //如果是管理员操作则添加消息
            ninMessageService.addBatchMsg(Collections.singletonList(teacherId), null, MsgEnum.ADD_COURSE, course.getCourseName());
        }

        ninArrangeMapper.updateById(arrange);

        NinTeacherCourse ninTeacherCourse = new NinTeacherCourse();
        ninTeacherCourse.setTeacherId(teacherId);
        ninTeacherCourse.setCourseId(courseId);
        return save(ninTeacherCourse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delSingle(Long teacherId, Long courseId) {
        //删除教师-课程记录
        NinTeacherCourse ninTeacherCourse = getOne(new LambdaQueryWrapper<NinTeacherCourse>()
                        .eq(NinTeacherCourse::getTeacherId, teacherId)
                        .eq(NinTeacherCourse::getCourseId, courseId));
        NinArrange ninArrange = ninArrangeMapper.selectOne(new LambdaQueryWrapper<NinArrange>()
                .eq(NinArrange::getTeacherId, ninTeacherCourse.getTeacherId())
                .eq(NinArrange::getCourseId, ninTeacherCourse.getCourseId()));
        //该记录是选修时，修改，将教师信息置空
        if (ninArrange != null && ninArrange.getMust() == 0) {
            ninArrangeMapper.updateTeaNullById(ninArrange.getId());
        }

        if (UserTypeEnum.ADMIN.getName().equals(UserUtil.getUserInfo().getUserType())) {
            //如果是管理员操作则添加消息
            NinCourse course = ninCourseMapper.selectById(courseId);
            ninMessageService.addBatchMsg(Collections.singletonList(teacherId), null, MsgEnum.DEL_COURSE, course.getCourseName());
        }

        return removeById(ninTeacherCourse.getId());
    }
}
