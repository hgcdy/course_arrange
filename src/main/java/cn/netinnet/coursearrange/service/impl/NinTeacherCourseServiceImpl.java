package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.NinArrange;
import cn.netinnet.coursearrange.entity.NinCourse;
import cn.netinnet.coursearrange.entity.NinHouse;
import cn.netinnet.coursearrange.entity.NinTeacherCourse;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinArrangeMapper;
import cn.netinnet.coursearrange.mapper.NinCourseMapper;
import cn.netinnet.coursearrange.mapper.NinHouseMapper;
import cn.netinnet.coursearrange.mapper.NinTeacherCourseMapper;
import cn.netinnet.coursearrange.service.INinTeacherCourseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private NinTeacherCourseMapper ninTeacherCourseMapper;
    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private NinArrangeMapper ninArrangeMapper;
    @Autowired
    private NinHouseMapper ninHouseMapper;

    @Override
    public List<ContactCourseBo> getSelectList(Long teacherId) {
        List<ContactCourseBo> list = ninTeacherCourseMapper.getSelectList(teacherId);
        return list;
    }

    @Override
    public int addSingle(Long teacherId, Long courseId) {
        List<NinTeacherCourse> ninTeacherCourses = ninTeacherCourseMapper.selectList(new QueryWrapper<>(new NinTeacherCourse() {{
            setTeacherId(teacherId);
        }}));
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
            List<NinArrange> ninArranges = ninArrangeMapper.selectList(new QueryWrapper<>(new NinArrange() {{
                setCourseId(courseId);
            }}));
            NinArrange arrange = ninArranges.get(0);

            //教师
            arrange.setTeacherId(teacherId);

            List<NinArrange> ninArranges1 = ninArrangeMapper.selectList(new QueryWrapper<>());
            int[][] taskCourseTime = ApplicationConstant.TASK_COURSE_TIME;

            //人数
            int num = course.getMaxClassNum() * ApplicationConstant.CLASS_PEOPLE_NUM;
            List<Long> houseIdList = ninHouseMapper.selectList(new QueryWrapper<>(new NinHouse() {{
                setHouseType(course.getHouseType());
            }})).stream().filter(i -> i.getSeat() >= num).map(NinHouse::getId).collect(Collectors.toList());
            Boolean bo = false;
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
                        if (collect == null) {
                            size = 0;
                        } else {
                            size = collect.size();
                        }
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

        return ninTeacherCourseMapper.insert(ninTeacherCourse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delSingle(Long teacherId, Long courseId) {
        //删除教师-课程记录
        NinTeacherCourse ninTeacherCourse = ninTeacherCourseMapper
                .selectOne(new LambdaQueryWrapper<NinTeacherCourse>()
                        .eq(NinTeacherCourse::getTeacherId, teacherId)
                        .eq(NinTeacherCourse::getCourseId, courseId));
        NinArrange ninArrange = ninArrangeMapper.selectOne(new QueryWrapper<>(new NinArrange() {{
            setTeacherId(ninTeacherCourse.getTeacherId());
            setCourseId(ninTeacherCourse.getCourseId());
        }}));
        //该记录是选修时，修改，将教师等信息置空
        if (ninArrange != null && ninArrange.getMust() == 0) {
            ninArrangeMapper.updateNullById(ninArrange.getId());
        }
        return ninTeacherCourseMapper.deleteById(ninTeacherCourse.getId());
    }
}
