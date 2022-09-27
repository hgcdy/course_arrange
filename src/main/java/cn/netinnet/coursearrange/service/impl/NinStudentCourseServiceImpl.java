package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import cn.netinnet.coursearrange.util.Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private NinStudentMapper ninStudentMapper;
    @Autowired
    private NinArrangeMapper ninArrangeMapper;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinCareerCourseMapper ninCareerCourseMapper;

    @Override
    public List<List<Map<String, Object>>> getSelectList(Long studentId) {
        Long classId = ninStudentMapper.selectById(studentId).getClassId();
        List<Map<String, Object>> list1 = ninStudentCourseMapper.getSelectList(studentId);
        Long careerId = ninClassMapper.selectById(classId).getCareerId();
        List<Map<String, Object>> list2 = ninCareerCourseMapper.getSelectList(careerId);
        Utils.conversion(list1);
        Utils.conversion(list2);
        return new ArrayList<List<Map<String, Object>>>(){{
            add(list1);
            add(list2);
        }};
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addSingle(NinStudentCourse ninStudentCourse) {
        //判断学生是否已经有选修
        List<NinStudentCourse> ninStudentCourses = ninStudentCourseMapper.selectList(new QueryWrapper<>(new NinStudentCourse() {{
            setStudentId(ninStudentCourse.getStudentId());
        }}));
        if (ninStudentCourses != null && ninStudentCourses.size() != 0){
            if (ninStudentCourses.size() >= 2){
                throw new ServiceException(412, "该学生已经选修两门课程了！");
            }
            //判断课程是否已经被选修
            List<Long> courseIds = ninStudentCourses.stream().map(i -> {
                Long courseId = i.getCourseId();
                return courseId;
            }).collect(Collectors.toList());
            if (courseIds.contains(ninStudentCourse.getCourseId())){
                throw new ServiceException(412, "该学生已经选修了这门课程！");
            }
        }

        NinArrange arrange = ninArrangeMapper.selectOne(new QueryWrapper<>(new NinArrange() {{
            setCourseId(ninStudentCourse.getCourseId());
        }}));

        //获取班级id
        Long classId = arrange.getClassId();

        //排课信息的人数+1
        arrange.setPeopleNum(arrange.getPeopleNum() + 1);
        ninArrangeMapper.updateById(arrange);

        //班级人数+1
        ninClassMapper.addPeopleNum(classId);

        ninStudentCourse.setId(IDUtil.getID());
        ninStudentCourse.setModifyUserId(UserUtil.getUserInfo().getUserId());
        ninStudentCourse.setCreateUserId(UserUtil.getUserInfo().getUserId());
        //选修教学班id
        ninStudentCourse.setTakeClassId(classId);

        return ninStudentCourseMapper.insert(ninStudentCourse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delSingle(Long id) {
        //班级人数-1
        NinStudentCourse ninStudentCourse = ninStudentCourseMapper.selectById(id);
        ninClassMapper.subPeopleNum(ninStudentCourse.getTakeClassId());

        NinArrange arrange = ninArrangeMapper.selectOne(new QueryWrapper<>(new NinArrange() {{
            setClassId(ninStudentCourse.getTakeClassId());
        }}));

        //排课信息的人数+1
        arrange.setPeopleNum(arrange.getPeopleNum() + 1);
        ninArrangeMapper.updateById(arrange);

        return ninStudentCourseMapper.deleteById(id);
    }
}
