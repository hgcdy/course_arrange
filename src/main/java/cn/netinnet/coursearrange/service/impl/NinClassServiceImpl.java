package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinClassService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import cn.netinnet.coursearrange.util.Utils;
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
public class NinClassServiceImpl extends ServiceImpl<NinClassMapper, NinClass> implements INinClassService {

    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinStudentMapper ninStudentMapper;
    @Autowired
    private NinCareerMapper ninCareerMapper;
    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private NinCareerCourseMapper ninCareerCourseMapper;
    @Autowired
    private NinClassCourseMapper ninClassCourseMapper;
    @Autowired
    private NinStudentCourseMapper ninStudentCourseMapper;
    @Autowired
    private NinArrangeMapper ninArrangeMapper;

    @Override
    public Map<String, Object> getPageSelectList(Integer page, Integer size, String college, Long careerId, String className) {
        PageHelper.startPage(page, size);
        List<Map<String, Object>> list = ninClassMapper.getSelectList(college, careerId, className);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        Utils.conversion(pageInfo.getList());
        HashMap<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return map;
    }

    @Override
    public List<Map<String, Object>> getSelectList(Long classId) {
        NinClass ninClass = ninClassMapper.selectById(classId);
        List<Map<String, Object>> list = new ArrayList<>();
        if (ninClass.getCareerId() == 0) {
            //选修
            List<NinArrange> ninArranges = ninArrangeMapper.selectList(new QueryWrapper<>(new NinArrange() {{
                setClassId(classId);
            }}));
            NinCourse course = ninCourseMapper.selectById(ninArranges.get(0).getCourseId());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("courseName", course.getCourseName());
            hashMap.put("houseType", course.getHouseType());
            hashMap.put("must", course.getMust());
            hashMap.put("courseTime", course.getCourseTime());
            hashMap.put("startTime", course.getStartTime());
            hashMap.put("endTime", course.getEndTime());
            hashMap.put("weekTime", course.getWeekTime());
            list.add(hashMap);
        } else {
            list = ninCareerCourseMapper.getSelectList(ninClass.getCareerId());
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getClassList(String college, Long careerId) {
        return ninClassMapper.getClassList(college, careerId);
    }


    @Override
    public Map<String, Map<String, List<Map<String, Object>>>> collegeCareerClassList() {
        List<Map<String, Object>> list = ninClassMapper.collegeCareerClassList();
        Utils.conversion(list);
        Map<String, Map<String, List<Map<String, Object>>>> map = list.stream().collect(Collectors.groupingBy(i -> (String) (i.get("college")), Collectors.groupingBy(i -> (String) i.get("careerName"))));
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addSingle(NinClass ninClass) {
        //同名验证
        Integer integer = ninClassMapper.selectCount(
                new QueryWrapper<NinClass>()
                        .eq("class_name", ninClass.getClassName()));
        if (integer > 0) {
            throw new ServiceException(412, "重名");
        }

        ninCareerMapper.addClassNum(ninClass.getCareerId());
        ninClass.setId(IDUtil.getID());
        ninClass.setCreateUserId(UserUtil.getUserInfo().getUserId());
        ninClass.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninClassMapper.insert(ninClass);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delBatchStudent(Long classId) {
        //选修班级
        NinClass ninClass = ninClassMapper.selectById(classId);
        if (ninClass.getCareerId() == 0) {
            //选修找学生-课程表，对应的记录删除
            return ninStudentCourseMapper.delete(
                    new QueryWrapper<>(new NinStudentCourse() {{
                        setTakeClassId(classId);
                    }}));
        } else {
            List<NinStudent> ninStudents = ninStudentMapper.selectList(new QueryWrapper<>(new NinStudent() {{
                setClassId(classId);
            }}));

            //学生id列表
            List<Long> studentIdList = ninStudents.stream().map(NinStudent::getId).collect(Collectors.toList());
            if (studentIdList != null && studentIdList.size() != 0) {
                //根据学生ids删除学生-课程表记录
                ninStudentCourseMapper.delBatchStudentId(studentIdList);
            }

            //删除学生
            return ninStudentMapper.delete(new QueryWrapper<>(new NinStudent() {{
                setClassId(classId);
            }}));

        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delById(Long id) {
        //todo 未测试
        NinClass ninClass = ninClassMapper.selectById(id);
        if (ninClass.getCareerId() == 0) {
            //选修班级
            //删除学生选课信息
            ninStudentCourseMapper.delete(new QueryWrapper<>(new NinStudentCourse() {{
                setTakeClassId(ninClass.getId());
            }}));
        } else {
            //删除班级下的所有学生及学生选课信息，同时学生选课对应的选修班级人数-1
            //根据班级id获取该班级的学生列表
            List<NinStudent> ninStudents = ninStudentMapper.selectList(new QueryWrapper<>(new NinStudent() {{
                setClassId(id);
            }}));
            if (ninStudents != null && ninStudents.size() != 0) {
                //该班级有学生
                //1.批量删除学生
                List<Long> studentIds = ninStudents.stream().map(NinStudent::getId).collect(Collectors.toList());
                ninStudentMapper.delBatchStudent(studentIds);
                //2.学生列表得到选课列表---》  Map<选修班id, 人数>
                List<NinStudentCourse> ninStudentCourseList = ninStudentCourseMapper.getStudentIds(studentIds);
                if (ninStudentCourseList != null && ninStudentCourseList.size() != 0) {
                    Map<Long, Long> map = ninStudentCourseList.stream().map(NinStudentCourse::getTakeClassId).collect(Collectors.groupingBy(i -> i, Collectors.counting()));
                    //获取选修班级id列表
                    ArrayList<Long> takeClassIds = new ArrayList<>(map.size());
                    for (Map.Entry<Long, Long> m : map.entrySet()) {
                        takeClassIds.add(m.getKey());
                    }
                    //获取班级列表
                    List<NinClass> ninClasses = ninClassMapper.selectBatchIds(takeClassIds);
                    for (NinClass clazz : ninClasses) {
                        clazz.setPeopleNum(clazz.getPeopleNum() - map.get(clazz.getId()).intValue());
                    }
                    //3.批量选修班人数减掉
                    ninClassMapper.subBatchPeopleNum(ninClasses);
                    //4.批量删除学生选课表
                    ninStudentCourseMapper.delBatchStudentId(studentIds);
                }
            }
        }
        //删除班级
        int i = ninClassMapper.deleteById(id);
        //专业的班级数量-1
        ninCareerMapper.subClassNum(ninClass.getCareerId());
        return i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int alterSingle(NinClass ninClass) {
        //同名验证
        Integer integer = ninClassMapper.selectCount(
                new QueryWrapper<NinClass>()
                        .eq("class_name", ninClass.getClassName())
                        .ne("id", ninClass.getId()));
        if (integer > 0) {
            throw new ServiceException(412, "重名");
        }
        NinClass ninClass1 = ninClassMapper.selectById(ninClass.getId());
        //如果修改所属的专业
        if (ninClass1.getCareerId() != ninClass.getCareerId()) {
            ninCareerMapper.addClassNum(ninClass.getCareerId());
            ninCareerMapper.subClassNum(ninClass1.getCareerId());
        }
        ninClass.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninClassMapper.updateById(ninClass);
    }


}
