package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinClassCourse;
import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.entity.NinTeacherCourse;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinTeacherCourseMapper;
import cn.netinnet.coursearrange.service.INinTeacherCourseService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import cn.netinnet.coursearrange.util.Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

        for (NinTeacherCourse ntc:
             ninTeacherCourses) {
            if (ntc.getCourseId() == ninTeacherCourse.getCourseId()){
                throw new ServiceException(412, "该教师已经选择这门课程了");
            }
        }

        ninTeacherCourse.setId(IDUtil.getID());
        ninTeacherCourse.setCreateUserId(UserUtil.getUserInfo().getUserId());
        ninTeacherCourse.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninTeacherCourseMapper.insert(ninTeacherCourse);
    }

    @Override
    public int delById(Long id) {
        return ninTeacherCourseMapper.deleteById(id);
    }
}
