package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.enums.OpenStateEnum;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinTeacherCourseService;
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
    private NinHouseMapper ninHouseMapper;
    @Autowired
    private NinSettingMapper ninSettingMapper;


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
            if (stateMap.get(courseId) != null) {
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
        if (ninTeacherCourses.size() >= ApplicationConstant.TEACHER_COURSE_NUM){
            throw new ServiceException(412, "该教师选课数量已经上限");
        }

        for (NinTeacherCourse ntc: ninTeacherCourses) {
            if (ntc.getCourseId().equals(courseId)){
                throw new ServiceException(412, "该教师已经选择这门课程了");
            }
        }

        NinCourse course = ninCourseMapper.selectById(courseId);
        if (course.getMust() == 0) {

            NinArrange arrange = ninArrangeMapper.selectOne(new LambdaQueryWrapper<NinArrange>().eq(NinArrange::getCourseId, courseId));

            //教师
            arrange.setTeacherId(teacherId);

            List<NinArrange> ninArranges1 = ninArrangeMapper.selectList(new QueryWrapper<>());
            int[][] taskCourseTime = ApplicationConstant.TASK_COURSE_TIME;

            //人数
            int num = course.getMaxClassNum() * ApplicationConstant.CLASS_PEOPLE_NUM;
            List<Long> houseIdList = ninHouseMapper.selectList(new QueryWrapper<>(new NinHouse() {{
                setHouseType(course.getHouseType());
            }})).stream().filter(i -> i.getSeat() >= num).map(NinHouse::getId).collect(Collectors.toList());
            boolean bo = false;
            //判断是否冲突
            ok: for (Long houseId : houseIdList) {
                for (int[] time : taskCourseTime) {
                    int size = 1;
                    if (ninArranges1 != null) {
                        List<NinArrange> collect = ninArranges1.stream().filter(i -> {
                            if (((i.getTeacherId() != null && i.getTeacherId().equals(teacherId))
                                    || (i.getHouseId() != null && i.getHouseId().equals(houseId)))
                                    && i.getWeek() == time[0]
                                    && i.getPitchNum() == time[1]) {
                                return true;
                            } else {
                                return false;
                            }
                        }).collect(Collectors.toList());
                        size = collect.size();
                    }
                    if (ninArranges1 == null || size == 0) {
                        bo = true;
                        arrange.setHouseId(houseId);
                        arrange.setWeek(time[0]);
                        arrange.setPitchNum(time[1]);
                        ninArrangeMapper.updateById(arrange);
                        break ok;
                    }
                }
            }
            if (!bo) {
                throw new ServiceException(412, "没有合适的教室或时间安排该课程");
            }
        }
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
        //该记录是选修时，修改，将教师等信息置空
        if (ninArrange != null && ninArrange.getMust() == 0) {
            ninArrangeMapper.updateNullById(ninArrange.getId());
        }
        return removeById(ninTeacherCourse.getId());
    }
}
