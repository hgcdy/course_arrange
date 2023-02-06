package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.bo.CourseBo;
import cn.netinnet.coursearrange.bo.SettingBo;
import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.domain.UserInfo;
import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.enums.CourseTypeEnum;
import cn.netinnet.coursearrange.enums.HouseTypeEnum;
import cn.netinnet.coursearrange.enums.OpenStateEnum;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinCourseService;
import cn.netinnet.coursearrange.service.INinSettingService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import cn.netinnet.coursearrange.util.CnUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
public class NinCourseServiceImpl extends ServiceImpl<NinCourseMapper, NinCourse> implements INinCourseService {

    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinHouseMapper ninHouseMapper;
    @Autowired
    private NinArrangeMapper ninArrangeMapper;
    @Autowired
    private NinSettingMapper ninSettingMapper;
    @Autowired
    private INinSettingService ninSettingService;
    @Autowired
    private NinStudentCourseMapper ninStudentCourseMapper;
    @Autowired
    private NinTeacherCourseMapper ninTeacherCourseMapper;
    @Autowired
    private NinCareerCourseMapper ninCareerCourseMapper;

    @Override
    public Map<String, Object> getPageSelectList(Integer page, Integer size, NinCourse ninCourse) {

        PageHelper.startPage(page, size);
        List<CourseBo> list = ninCourseMapper.getSelectList(ninCourse);
        PageInfo<CourseBo> pageInfo = new PageInfo<>(list);

        pageInfo.getList().forEach(i -> {
            if (i.getMust() != null) {
                i.setCnMust(CourseTypeEnum.codeOfKey(i.getMust()).getName());
            }
            if (i.getHouseType() != null) {
                i.setCnHouseType(HouseTypeEnum.codeOfKey(i.getHouseType()).getName());
            }
        });

        HashMap<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return map;
    }

    @Override
    public NinCourse getCourseById(Long id) {
        return getById(id);
    }

    @Override
    public CourseBo getCourseAndState(Long id, String userType) {
        CourseBo courseBo = new CourseBo();
        NinCourse course = getById(id);
        BeanUtils.copyProperties(course, courseBo);
        NinSetting ninSetting = ninSettingMapper.selectOne(new LambdaQueryWrapper<NinSetting>()
                .select(NinSetting::getOpenState)
                .eq(NinSetting::getUserType, userType)
                .eq(NinSetting::getCourseId, id));
        if (null == ninSetting || UserTypeEnum.CLAZZ.getName().equals(userType)) {
            courseBo.setStart("不可修改");
        } else {
            courseBo.setStart(OpenStateEnum.codeOfKey(ninSetting.getOpenState()).getName());
        }
        courseBo.setCnHouseType(HouseTypeEnum.codeOfKey(courseBo.getHouseType()).getName());
        courseBo.setCnMust(CourseTypeEnum.codeOfKey(courseBo.getMust()).getName());

        return courseBo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addSingle(NinCourse ninCourse) {

        //同名验证
        int count = count(new LambdaQueryWrapper<NinCourse>().eq(NinCourse::getCourseName, ninCourse.getCourseName()));
        if (count > 0) {
            throw new ServiceException(412, "重名");
        }


        List<NinHouse> houses = ninHouseMapper.selectList(new LambdaQueryWrapper<NinHouse>()
                .eq(NinHouse::getHouseType, ninCourse.getHouseType()).orderByDesc(NinHouse::getSeat));
        if (houses != null && houses.size() != 0) {
            List<Integer> list = houses.stream().map(NinHouse::getSeat).filter(i -> i >= ninCourse.getMaxClassNum() * ApplicationConstant.CLASS_PEOPLE_NUM).collect(Collectors.toList());
            if (list.isEmpty()) {
                throw new ServiceException(412, "没有可容纳" + ninCourse.getMaxClassNum() + "个班级一起上课的" + HouseTypeEnum.codeOfKey(ninCourse.getHouseType()).getName());
            }
        } else {
            throw new ServiceException(412, "没有可容纳" + ninCourse.getMaxClassNum() + "个班级一起上课的" + HouseTypeEnum.codeOfKey(ninCourse.getHouseType()).getName());
        }


        int i = ninCourseMapper.insert(ninCourse);

        //教师权限记录
        NinSetting ninSetting = new NinSetting();
        ninSetting.setCourseId(ninCourse.getId());
        ninSetting.setCourseName(ninCourse.getCourseName());
        ninSetting.setUserType(UserTypeEnum.TEACHER.getName());
        ninSetting.setOpenState(0);
        ninSettingMapper.insert(ninSetting);

        //如果是选修课程
        if (ninCourse.getMust() == 0) {
            //生成选修教学班
            NinClass ninClass = new NinClass();
            ninClass.setCareerId(0L);
            ninClass.setClassName(ninCourse.getCourseName() + "选修班");
            ninClass.setCourseNum(1);
            ninClassMapper.insert(ninClass);

            //生成Arrange
            NinArrange arrange = new NinArrange();
            arrange.setCareerId(0L);
            arrange.setClassId(ninClass.getId());
            arrange.setCourseId(ninCourse.getId());
            arrange.setMust(0);
            arrange.setWeekly(0);
            arrange.setStartTime(ninCourse.getStartTime() != null ? ninCourse.getStartTime() : 1);
            arrange.setEndTime(ninCourse.getEndTime() != null ? ninCourse.getEndTime() : 16);
            arrange.setPeopleNum(0);
            arrange.setDelFlag(-2);
            ninArrangeMapper.insert(arrange);

            //生成学生权限记录
            ninSetting.setId(IDUtil.getID());
            ninSetting.setUserType(UserTypeEnum.STUDENT.getName());
            ninSettingMapper.insert(ninSetting);
        }
        return i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delById(Long id) {
        NinCourse course = getById(id);
        //删除其他表有关该课程的记录

        //删除教师-课程
        ninTeacherCourseMapper.delete(new LambdaQueryWrapper<NinTeacherCourse>().eq(NinTeacherCourse::getCourseId, id));
        //删除选课权限表
        ninSettingMapper.delete(new LambdaQueryWrapper<NinSetting>().eq(NinSetting::getCourseId, id));

        if (course.getMust() == 0) {
            //删除学生-课程
            ninStudentCourseMapper.delete(new LambdaQueryWrapper<NinStudentCourse>().eq(NinStudentCourse::getCourseId, id));

            //课程id查询排课信息
            NinArrange arrange = ninArrangeMapper.selectOne(new LambdaQueryWrapper<NinArrange>().eq(NinArrange::getCourseId, id));
            //获取班级id
            Long classId = arrange.getClassId();
            //删除选修班级
            ninClassMapper.deleteById(classId);

        } else {

            //获取有选择该课程的专业列表
            List<NinCareerCourse> ninCareerCourses = ninCareerCourseMapper.selectList(new LambdaQueryWrapper<NinCareerCourse>()
                    .select(NinCareerCourse::getCareerId).eq(NinCareerCourse::getCourseId, id));
            if (ninCareerCourses != null && !ninCareerCourses.isEmpty()) {
                //获取专业id并去重
                List<Long> careerIdList = ninCareerCourses.stream().map(NinCareerCourse::getCareerId).distinct().collect(Collectors.toList());

                ArrayList<Map<String, Object>> maps = new ArrayList<>();
                for (Long careerId : careerIdList) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("careerId", careerId);
                    hashMap.put("courseNum", -1);
                    maps.add(hashMap);
                }
                //班级课程数-1
                ninClassMapper.alterBatchCourseNum(maps);
                //删除专业-课程表
                ninCareerCourseMapper.delete(new LambdaQueryWrapper<NinCareerCourse>().eq(NinCareerCourse::getCourseId, id));
            }
        }
        //删除课程
        return ninCourseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int alterSingle(NinCourse ninCourse) {
        //同名验证
        int count = count(new LambdaQueryWrapper<NinCourse>()
                .eq(NinCourse::getCourseName, ninCourse.getCourseName())
                .ne(NinCourse::getId, ninCourse.getId()));
        if (count > 0) {
            throw new ServiceException(412, "重名");
        }
        ninSettingService.update(new LambdaUpdateWrapper<NinSetting>()
                .set(NinSetting::getCourseName, ninCourse.getCourseName())
                .eq(NinSetting::getCourseId, ninCourse.getId()));
        return ninCourseMapper.updateById(ninCourse);
    }

    @Override
    public List<NinCourse> getCourseList(Integer must) {
        return ninCourseMapper.selectList(new LambdaQueryWrapper<NinCourse>()
                .select(NinCourse::getId, NinCourse::getCourseName)
                .eq(null != must, NinCourse::getMust, must));
    }

}
