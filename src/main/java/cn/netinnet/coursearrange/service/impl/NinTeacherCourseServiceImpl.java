package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.NinArrange;
import cn.netinnet.coursearrange.entity.NinCourse;
import cn.netinnet.coursearrange.entity.NinTeacherCourse;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinArrangeMapper;
import cn.netinnet.coursearrange.mapper.NinCourseMapper;
import cn.netinnet.coursearrange.mapper.NinTeacherCourseMapper;
import cn.netinnet.coursearrange.service.INinTeacherCourseService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import cn.netinnet.coursearrange.util.Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    @Override
    public List<Map<String, Object>> getSelectList(Long teacherId) {
        List<Map<String, Object>> list = ninTeacherCourseMapper.getSelectList(teacherId);
        Utils.conversion(list);
        return list;
    }

    @Override
    public int addSingle(NinTeacherCourse ninTeacherCourse) {
        List<NinTeacherCourse> ninTeacherCourses = ninTeacherCourseMapper.selectList(new QueryWrapper<>(new NinTeacherCourse() {{
            setTeacherId(ninTeacherCourse.getTeacherId());
        }}));
        if (ninTeacherCourses.size() >= 2){
            throw new ServiceException(412, "该教师已经有两门授课课程了");
        }

        for (NinTeacherCourse ntc: ninTeacherCourses) {
            if (ntc.getCourseId().equals(ninTeacherCourse.getCourseId())){
                throw new ServiceException(412, "该教师已经选择这门课程了");
            }
        }

        NinCourse course = ninCourseMapper.selectById(ninTeacherCourse.getCourseId());
        if (course.getMust() == 0) {
            List<NinArrange> ninArranges = ninArrangeMapper.selectList(new QueryWrapper<>(new NinArrange() {{
                setCourseId(ninTeacherCourse.getCourseId());
            }}));
            NinArrange arrange = ninArranges.get(0);
            arrange.setTeacherId(ninTeacherCourse.getTeacherId());
            List<NinArrange> ninArranges1 = ninArrangeMapper.selectList(new QueryWrapper<>(new NinArrange() {{
                setCourseId(ninTeacherCourse.getTeacherId());
            }}));
            int[][] taskCourseTime = ApplicationConstant.TASK_COURSE_TIME;
            if (ninArranges1 != null && ninArranges1.size() != 0) {
                arrange.setWeek(taskCourseTime[1][0]);
                arrange.setPitchNum(taskCourseTime[1][1]);
            } else {
                arrange.setWeek(taskCourseTime[0][0]);
                arrange.setPitchNum(taskCourseTime[0][1]);
            }
            ninArrangeMapper.updateById(arrange);
        }

        ninTeacherCourse.setId(IDUtil.getID());
        ninTeacherCourse.setCreateUserId(UserUtil.getUserInfo().getUserId());
        ninTeacherCourse.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninTeacherCourseMapper.insert(ninTeacherCourse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delById(Long id) {
        NinTeacherCourse ninTeacherCourse = ninTeacherCourseMapper.selectById(id);
        NinArrange ninArrange = ninArrangeMapper.selectOne(new QueryWrapper<>(new NinArrange() {{
            setTeacherId(ninTeacherCourse.getTeacherId());
            setCourseId(ninTeacherCourse.getCourseId());
        }}));
        if (ninArrange.getMust() == 0) {
            ninArrange.setTeachClassId(null);
            ninArrange.setWeek(null);
            ninArrange.setPitchNum(null);
            ninArrangeMapper.updateById(ninArrange);
        }
        return ninTeacherCourseMapper.deleteById(id);
    }
}
