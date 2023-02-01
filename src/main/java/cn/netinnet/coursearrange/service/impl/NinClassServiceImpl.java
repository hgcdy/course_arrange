package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.bo.ClassBo;
import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.enums.CourseTypeEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinClassService;
import cn.netinnet.coursearrange.service.INinSettingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private NinStudentCourseMapper ninStudentCourseMapper;
    @Autowired
    private NinTeacherCourseMapper ninTeacherCourseMapper;
    @Autowired
    private NinArrangeMapper ninArrangeMapper;
    @Autowired
    private INinSettingService ninSettingService;
    @Autowired
    private NinCareerCourseMapper ninCareerCourseMapper;

    @Override
    public Map<String, Object> getPageSelectList(Integer page, Integer size, String college, Long careerId, String className) {
        PageHelper.startPage(page, size);
        List<ClassBo> list = ninClassMapper.getSelectList(college, careerId, className);
        PageInfo<ClassBo> pageInfo = new PageInfo<>(list);
        HashMap<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return map;
    }



    @Override
    public List<NinClass> getClassList(String college, Long careerId) {
        List<Long> careerIdList = null;
        if (college != null && !college.equals("")) {
            List<NinCareer> ninCareers = ninCareerMapper.selectList(new LambdaQueryWrapper<NinCareer>().select(NinCareer::getCareerName).eq(NinCareer::getCollege, college));
            careerIdList = ninCareers.stream().map(NinCareer::getId).collect(Collectors.toList());
        } else {
            careerIdList = Collections.singletonList(careerId);
        }
        List<NinClass> list = list(new QueryWrapper<NinClass>().select("id, career_id, class_name").lambda().in(NinClass::getCareerId, careerIdList));
        return list;
    }

    @Override
    public Map<String, Map<String, List<ClassBo>>> collegeCareerClassList() {
        List<ClassBo> list = ninClassMapper.collegeCareerClassList();
        //按学院，专业名称分组
        return list.stream().collect(Collectors.groupingBy(ClassBo::getCollege, Collectors.groupingBy(ClassBo::getCareerName)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addSingle(NinClass ninClass) {
        //同名验证
        int count = count(new LambdaQueryWrapper<NinClass>().eq(NinClass::getClassName, ninClass.getClassName()));
        if (count > 0) {
            throw new ServiceException(412, "重名");
        }
        ninCareerMapper.addClassNum(ninClass.getCareerId());
        return save(ninClass);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delById(Long id) {
        NinClass ninClass = getById(id);
        if (ninClass.getCareerId() == 0) {
            //选修班级
            //删除学生选课信息
            ninStudentCourseMapper.delete(new LambdaQueryWrapper<NinStudentCourse>()
                    .eq(NinStudentCourse::getTakeClassId, ninClass.getId()));

            //删除课程
            NinArrange arrange = ninArrangeMapper.selectOne(new LambdaQueryWrapper<NinArrange>()
                    .eq(NinArrange::getClassId, ninClass.getId()));

            Long courseId = arrange.getCourseId();
            ninCourseMapper.deleteById(courseId);

            //根据课程id删除setting和定时器
            ninSettingService.delTimerByCourseId(courseId);

            //删除教师选课
            ninTeacherCourseMapper.delete(new LambdaQueryWrapper<NinTeacherCourse>()
                    .eq(NinTeacherCourse::getCourseId, courseId));


            //删除排课记录
            ninArrangeMapper.delete(new LambdaQueryWrapper<NinArrange>()
                    .eq(NinArrange::getCourseId, courseId));

        } else {
            //删除班级下的所有学生及学生选课信息，同时学生选课对应的选修班级人数-1

            //根据班级id获取该班级的学生列表
            List<NinStudent> ninStudents = ninStudentMapper.selectList(new LambdaQueryWrapper<NinStudent>().eq(NinStudent::getClassId, id));

            if (ninStudents != null && !ninStudents.isEmpty()) {
                //该班级有学生
                //1.批量删除学生
                List<Long> studentIds = ninStudents.stream().map(NinStudent::getId).collect(Collectors.toList());
                ninStudentMapper.deleteBatchIds(studentIds);
                //2.学生列表得到选课列表---》  Map<选修班id, 人数>
                List<NinStudentCourse> ninStudentCourseList = ninStudentCourseMapper
                        .selectList(new LambdaQueryWrapper<NinStudentCourse>()
                                .in(NinStudentCourse::getStudentId, studentIds));
                if (ninStudentCourseList != null && !ninStudentCourseList.isEmpty()) {
                    Map<Long, Long> map = ninStudentCourseList.stream().map(NinStudentCourse::getTakeClassId).collect(Collectors.groupingBy(i -> i, Collectors.counting()));

                    ArrayList<Map<String, Object>> maps = new ArrayList<>();
                    for (Map.Entry<Long, Long> m : map.entrySet()) {
                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put("classId", m.getKey());
                        map1.put("peopleNum", -(m.getValue().intValue()));
                        maps.add(map1);
                    }

                    //3.批量选修班人数减掉
                    ninClassMapper.alterBatchPeopleNum(maps);
                    //4.批量删除学生选课表
                    ninStudentCourseMapper.deleteBatchIds(studentIds);
                }
            }
        }

        //专业的班级数量-1
        ninCareerMapper.subClassNum(ninClass.getCareerId());
        //删除班级
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean alterSingle(NinClass ninClass) {
        //同名验证
        int count = count(new LambdaQueryWrapper<NinClass>()
                .eq(NinClass::getClassName, ninClass.getClassName())
                .ne(NinClass::getId, ninClass.getId()));

        if (count > 0) {
            throw new ServiceException(412, "重名");
        }
        NinClass ninClassOld = ninClassMapper.selectById(ninClass.getId());
        //如果修改所属的专业
        if (!Objects.equals(ninClassOld.getCareerId(), ninClass.getCareerId())) {
            ninCareerMapper.addClassNum(ninClass.getCareerId());
            ninCareerMapper.subClassNum(ninClassOld.getCareerId());
        }
        return updateById(ninClass);
    }

    @Override
    public Map<String, List<Map<String, Object>>> getCourse(Long id) {
        NinClass ninClass = getById(id);
        List<Map<String, Object>> selectedList = new ArrayList<>();
        List<NinCourse> courseList = ninCourseMapper.selectList(new LambdaQueryWrapper<NinCourse>()
                .select(NinCourse::getId, NinCourse::getCourseName));
        Map<Long, String> courseMap = courseList.stream().collect(Collectors.toMap(NinCourse::getId, NinCourse::getCourseName));

        if (ninClass.getCareerId() == 0) {
            NinArrange arrange = ninArrangeMapper.selectOne(new LambdaQueryWrapper<NinArrange>().select(NinArrange::getCourseId).eq(NinArrange::getClassId, id));
            selectedList.add(new HashMap<String, Object>(){{
                put("id", arrange.getCourseId().toString());
                put("name", courseMap.get(arrange.getCourseId()));
                put("isOk", false);
            }});

        } else {
            List<NinCareerCourse> ninCareerCourses = ninCareerCourseMapper
                    .selectList(new LambdaQueryWrapper<NinCareerCourse>()
                            .eq(NinCareerCourse::getCareerId, ninClass.getCareerId()));
            ninCareerCourses.forEach(i -> {
                Map<String, Object> map = new HashMap<>();
                Long courseId = i.getCourseId();
                map.put("id", courseId.toString());
                map.put("name", courseMap.get(courseId));
                map.put("isOk", false);
                selectedList.add(map);
            });
        }

        return new HashMap<String, List<Map<String, Object>>>() {{
            put("selected", selectedList);
            put("unselected", new ArrayList<Map<String, Object>>());
        }};
    }


}
