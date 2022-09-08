package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.entity.NinClassCourse;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinClassCourseMapper;
import cn.netinnet.coursearrange.mapper.NinClassMapper;
import cn.netinnet.coursearrange.service.INinClassCourseService;
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
public class NinClassCourseServiceImpl extends ServiceImpl<NinClassCourseMapper, NinClassCourse> implements INinClassCourseService {

    @Autowired
    private NinClassCourseMapper ninClassCourseMapper;
    @Autowired
    private NinClassMapper ninClassMapper;

    @Override
    public List<Map<String, Object>> getSelectList(Long classId) {
        List<Map<String, Object>> list = ninClassCourseMapper.getSelectList(classId);
        Utils.conversion(list);
        return list;
    }

    @Override
    public int addSingle(NinClassCourse ninClassCourse) {
        return 0;
    }

    @Override
    public int addBatch(String career, String className, Long courseId) {
        return 0;
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public int addSingle(NinClassCourse ninClassCourse) {
//        ninClassCourse.setId(IDUtil.getID());
//        ninClassCourse.setModifyUserId(UserUtil.getUserInfo().getUserId());
//        ninClassCourse.setCreateUserId(UserUtil.getUserInfo().getUserId());
//        //记录重复验证
//        Integer i = ninClassCourseMapper.selectCount(new QueryWrapper<>(new NinClassCourse() {{
//            setCourseId(ninClassCourse.getCourseId());
//            setClassId(ninClassCourse.getClassId());
//        }}));
//        if (i == 1) {
//            return i;
//        }
//        NinClass ninClass = ninClassMapper.selectById(ninClassCourse.getClassId());
//        if (ninClass.getCareer().equals("#")) {
//            if (ninClass.getCourseNum() == 1) {
//                //选修班级只能有一门选修课
//                throw new ServiceException(412, "该选修教学班已经安排好课程了");
//            }
//        }
//        //班级选择课程+1（有报错则回滚）
//        ninClass.setCourseNum(ninClass.getCourseNum() + 1);
//        ninClassMapper.updateById(ninClass);
//
//        return ninClassCourseMapper.insert(ninClassCourse);
//
//    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public int addBatch(String career, String className, Long courseId) {
//        List<NinClass> selectList = ninClassMapper.getSelectList(career, className);
//        //获得要添加课程的班级id列表(除选修班外)
//        List<Long> classIdList = selectList.stream().filter(i -> !(i.getCareer().equals("#"))).map(i -> {
//            Long classId = i.getId();
//            return classId;
//        }).collect(Collectors.toList());
//        //已经选择该课程的班级列表
//        List<NinClassCourse> ninClassCourses = ninClassCourseMapper.selectList(new QueryWrapper<>(new NinClassCourse() {{
//            setCourseId(courseId);
//        }}));
//        //已经选择该课程的班级id列表
//        List<Long> classIds = ninClassCourses.stream().map(i -> {
//            Long classId = i.getClassId();
//            return classId;
//        }).collect(Collectors.toList());
//        //删除选择该课程的班级id
//        classIdList.removeAll(classIds);
//        if (classIdList.size() == 0) {
//            throw new ServiceException(200, "所选班级已有该课程");
//        }
//        //生成实体类列表
//        ArrayList<NinClassCourse> ninClassCourseList = new ArrayList<>(classIdList.size());
//        for (Long classId :
//                classIdList) {
//            NinClassCourse ninClassCourse = new NinClassCourse();
//            ninClassCourse.setId(IDUtil.getID());
//            ninClassCourse.setCourseId(courseId);
//            ninClassCourse.setClassId(classId);
//            ninClassCourse.setCreateUserId(UserUtil.getUserInfo().getUserId());
//            ninClassCourse.setModifyUserId(UserUtil.getUserInfo().getUserId());
//            ninClassCourseList.add(ninClassCourse);
//        }
//        int i = ninClassCourseMapper.addBatch(ninClassCourseList);
//        //班级课程数+1
//        ninClassMapper.addBatchCourseNum(classIdList);
//        return i;
//    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delById(Long id) {
        NinClassCourse ninClassCourse = ninClassCourseMapper.selectById(id);
        NinClass ninClass = ninClassMapper.selectById(ninClassCourse.getClassId());
        int i = ninClassCourseMapper.deleteById(id);
        //班级选择课程-1（有报错则回滚）
        ninClass.setCourseNum(ninClass.getCourseNum() - 1);
        ninClassMapper.updateById(ninClass);
        return i;
    }
}
