package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinCourseService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    private NinStudentCourseMapper ninStudentCourseMapper;
    @Autowired
    private NinClassCourseMapper ninClassCourseMapper;
    @Autowired
    private NinTeacherCourseMapper ninTeacherCourseMapper;

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


}
