package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.bo.ArrangeBo;
import cn.netinnet.coursearrange.bo.HouseApplyBo;
import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.enums.*;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.geneticAlgorithm.GeneticAlgorithm;
import cn.netinnet.coursearrange.geneticAlgorithm.domain.TaskRecord;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.service.INinHouseService;
import cn.netinnet.coursearrange.service.INinTeachClassService;
import cn.netinnet.coursearrange.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
@Slf4j
@Service
public class NinArrangeServiceImpl extends ServiceImpl<NinArrangeMapper, NinArrange> implements INinArrangeService {


    @Autowired
    private NinArrangeMapper ninArrangeMapper;
    @Autowired
    private NinTeacherMapper ninTeacherMapper;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinStudentMapper ninStudentMapper;
    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private INinHouseService ninHouseService;
    @Autowired
    private NinSettingMapper ninSettingMapper;
    @Autowired
    private NinTeacherCourseMapper ninTeacherCourseMapper;
    @Autowired
    private NinTeachClassMapper ninTeachClassMapper;
    @Autowired
    private NinStudentCourseMapper ninStudentCourseMapper;
    @Autowired
    private INinTeachClassService ninTeachClassService;
    @Autowired
    private NinMessageMapper ninMessageMapper;

    @Autowired
    private ApplicationContext applicationContext;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void arrange() {

        long oldData = System.currentTimeMillis();

        Integer courseCount = ninCourseMapper.selectCount(new QueryWrapper<NinCourse>());
        Integer selectCount = ninTeacherCourseMapper.selectCount(new QueryWrapper<NinTeacherCourse>().select("DISTINCT course_id"));
        if (courseCount > selectCount) {
            throw new ServiceException(412, "尚有课程还未选择，不能进行排课");
        }

        Integer settingCount = ninSettingMapper.selectCount(new LambdaQueryWrapper<NinSetting>()
                .eq(NinSetting::getUserType, UserTypeEnum.TEACHER.getName())
                .eq(NinSetting::getOpenState, OpenStateEnum.OPEN.getCode()));
        if (settingCount != 0) {
            throw new ServiceException(412, "排课前请先关闭教师选课通道");
        }

        //动态创建Bean
        GeneticAlgorithm geneticAlgorithm = (GeneticAlgorithm) applicationContext.getBean("geneticAlgorithmImpl");
        //遗传算法获取较优解
        List<TaskRecord> taskRecordList = geneticAlgorithm.start();

        Map<Long, NinCourse> courseIdCourseListMap = ninCourseMapper.selectList(new LambdaQueryWrapper<NinCourse>().eq(NinCourse::getMust, CourseTypeEnum.REQUIRED_COURSE.getCode())).stream().collect(Collectors.toMap(NinCourse::getId, Function.identity()));
        Map<Long, String> classIdClassNameMap = ninClassMapper.selectList(new LambdaQueryWrapper<NinClass>().select(NinClass::getId, NinClass::getClassName)).stream().collect(Collectors.toMap(NinClass::getId, NinClass::getClassName));
        HashMap<Long, List<Long>> teachClassIdClassIdListMap = new HashMap<>();

        //排课表结果集
        ArrayList<NinArrange> ninArrangeList = new ArrayList<>();
        //教学班结果集
        ArrayList<NinTeachClass> ninTeachClasses = new ArrayList<>();

        for (TaskRecord taskRecord : taskRecordList) {
            if (null == taskRecord.getTeaTask().getTeachClassId()) {
                continue;
            }
            NinArrange arrange = new NinArrange(taskRecord);
            NinCourse course = courseIdCourseListMap.get(arrange.getCourseId());
            if (arrange.getMust() == CourseTypeEnum.REQUIRED_COURSE.getCode()) {
                teachClassIdClassIdListMap.put(taskRecord.getTeaTask().getTeachClassId(), taskRecord.getTeaTask().getClassIdList());
            }
            Integer startTime = course.getStartTime();
            Integer endTime = course.getEndTime();
            Integer weekTime = course.getWeekTime();
            Integer weekly = arrange.getWeekly();

            int start, end;
            if (endTime - startTime > weekTime) {
                start = (int) (Math.random() * (endTime - weekTime - startTime)) + startTime;
                end = start + weekTime;
            } else {
                start = startTime;
                end = startTime + weekTime - 1;
            }

            if (weekly != 0) {
                if ((weekly + start % 2) == 1 || (weekly + start % 2) == 3) {
                    start ++;
                }
                if ((weekly + end % 2) == 1 || (weekly + end % 2) == 3) {
                    end--;
                }
            }
            arrange.setStartTime(startTime);
            arrange.setEndTime(endTime);
            ninArrangeList.add(arrange);
        }

        for (Map.Entry<Long, List<Long>> map : teachClassIdClassIdListMap.entrySet()) {
            List<Long> value = map.getValue();
            for (Long classId : value) {
                NinTeachClass ninTeachClass = new NinTeachClass();
                ninTeachClass.setClassId(classId);
                ninTeachClass.setTeachClassId(map.getKey());
                ninTeachClass.setClassName(classIdClassNameMap.get(classId));
                ninTeachClasses.add(ninTeachClass);
            }
        }

        //清除数据库
        empty();
        //持久化数据
        saveBatch(ninArrangeList);
        ninTeachClassService.saveBatch(ninTeachClasses);

        //计时
        long newData = System.currentTimeMillis();
        log.info("排课结束, 用时" + (newData - oldData) + "毫秒");
    }


    @Override
    public void empty() {
        //删除arrange表
        ninArrangeMapper.delete(new LambdaQueryWrapper<NinArrange>().ne(NinArrange::getCareerId, 0));
        //删除教学班表
        ninTeachClassMapper.delete(new QueryWrapper<>());
    }

    @Override
    public Map<String, Object> getPageSelectList(ArrangeBo bo, Integer page, Integer size) {
        //如果查询条件有班级
        if (bo.getClassId() != null) {
            NinClass ninClass = ninClassMapper.selectById(bo.getClassId());
            if (ninClass.getCareerId() != 0) {
                //如果不是选修，查询教学班id列表
                //如果是，则什么都不做
                List<Long> teachClassIdList = ninTeachClassMapper.selectList(new LambdaQueryWrapper<NinTeachClass>()
                        .eq(NinTeachClass::getClassId, ninClass.getId()))
                        .stream().map(NinTeachClass::getTeachClassId).collect(Collectors.toList());

                //添加教学班列表，将班级置空
                bo.setTeachClassIdList(teachClassIdList);
                bo.setClassId(null);
            }
        }

        PageHelper.startPage(page, size);
        List<ArrangeBo> list = ninArrangeMapper.getSelectList(bo);
        PageInfo<ArrangeBo> pageInfo = new PageInfo<>(list);

        pageInfo.getList().forEach(i -> {
            i.setCnWeek(CnUtil.cnWeek(i.getWeek()));
            i.setCnPitchNum(CnUtil.cnPitchNum(i.getPitchNum()));
            if (null != i.getMust()) {
                i.setCnMust(CourseTypeEnum.codeOfKey(i.getMust()).getName());
            }
            if (null != i.getWeekly()) {
                i.setCnWeekly(WeeklyTypeEnum.codeOfKey(i.getWeekly()).getName());
            }
        });

        HashMap<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        NinArrange arrange = getOne(new LambdaQueryWrapper<NinArrange>()
                .eq(NinArrange::getMust, CourseTypeEnum.REQUIRED_COURSE.getCode()), false);
        map.put("isOk", null != arrange);//是否排课过,未排课,选修可以进行编辑
        return map;
    }

    @Override
    public int delArrange(Long id) {
        NinArrange arrange = ninArrangeMapper.selectById(id);
        if (arrange.getMust() == 0) {

            //选修
            Long classId = arrange.getClassId();
            Long courseId = arrange.getCourseId();

            //删除班级
            ninClassMapper.deleteById(classId);

            //删除课程
            ninCourseMapper.deleteById(courseId);

            //删除学生选课记录
            ninStudentCourseMapper.delete(new LambdaQueryWrapper<NinStudentCourse>()
                    .eq(NinStudentCourse::getTakeClassId, classId));

            //删除教师选课记录
            ninTeacherCourseMapper.delete(new LambdaQueryWrapper<NinTeacherCourse>()
                    .eq(NinTeacherCourse::getCourseId, courseId));

            //删除设置记录
            ninSettingMapper.delete(new LambdaQueryWrapper<NinSetting>()
                    .eq(NinSetting::getCourseId, courseId));
        }
        return ninArrangeMapper.deleteById(id);
    }

    @Override
    public List<NinHouse> getHouseByArrangeId(Long id) {
        //两种情况：
        //1、排课前的选修课程
        //2、排课后的存在冲突的必修课程 -》班级存在，即需要设置教室的座位足够
        NinArrange arrange = getById(id);
        NinCourse course = ninCourseMapper.selectById(arrange.getCourseId());
        Integer houseType = course.getHouseType();
        LambdaQueryWrapper<NinHouse> wrapper = new LambdaQueryWrapper<NinHouse>()
                .eq(NinHouse::getHouseType, houseType);

        if (arrange.getMust() == CourseTypeEnum.REQUIRED_COURSE.getCode()) {
            wrapper.ge(NinHouse::getSeat, arrange.getPeopleNum());
        }
        return ninHouseService.list(wrapper);
    }

    @Override
    public boolean alterArrange(Long id, Long houseId, Integer week, Integer pitchNum) {
        NinArrange arrange = getById(id);
        Integer weekly = arrange.getWeekly();
        Long teacherId = arrange.getTeacherId();
        Integer must = arrange.getMust();

        //时间相同的排课记录
        List<NinArrange> list = list(new LambdaQueryWrapper<NinArrange>()
                .eq(NinArrange::getWeek, week)
                .eq(NinArrange::getPitchNum, pitchNum)
                .eq(NinArrange::getMust, must));

        //教学班-》班级列表
        Map<Long, List<Long>> teachClassMap = null;
        //要修改排课记录对应的班级id列表
        List<Long> classIdList = null;

        if (must.equals(CourseTypeEnum.REQUIRED_COURSE.getCode())) {
            //排课记录对应的教学班id
            Set<Long> teachClassIdList = list.stream().map(NinArrange::getTeachClassId).collect(Collectors.toSet());
            teachClassIdList.add(arrange.getTeachClassId());//加上要修改的排课记录对应的教学班id

            List<NinTeachClass> teachClassList = ninTeachClassMapper.selectList(new LambdaQueryWrapper<NinTeachClass>()
                    .in(NinTeachClass::getTeachClassId, teachClassIdList));
            //教学班-》班级列表
            teachClassMap = teachClassList.stream()
                    .collect(Collectors.toMap(NinTeachClass::getTeachClassId,i -> Collections.singletonList(i.getClassId()), (v1, v2) -> {v1.addAll(v2);return v1;}));
            //要修改排课记录对应的班级id列表
            classIdList = teachClassMap.get(arrange.getTeachClassId());
        }

        if (!list.isEmpty()) {
            for (NinArrange ninArrange : list) {
                Integer weekly1 = ninArrange.getWeekly();
                if (weekly != 0 && weekly1 != 0 && !weekly.equals(weekly1)) {
                    continue;
                }
                if (null != teacherId && null != ninArrange.getTeacherId() && teacherId.equals(ninArrange.getTeacherId())) {
                    throw new ServiceException(412, "教师冲突");
                }
                if (null != houseId && null != ninArrange.getHouseId() && houseId.equals(ninArrange.getHouseId())) {
                    throw new ServiceException(412, "教室冲突");
                }

                //必修判断教学班,选修不会有班级冲突
                if (null != teachClassMap) {
                    List<Long> classIds = teachClassMap.get(ninArrange.getTeachClassId());
                    if (!Collections.disjoint(classIds, classIdList)) {
                        throw new ServiceException(412, "班级冲突");
                    }
                }
            }
        }
        arrange.setDelFlag(0);
        arrange.setHouseId(houseId);
        arrange.setWeek(week);
        arrange.setPitchNum(pitchNum);
        return updateById(arrange);
    }

    @Override
    public Map<String, StringBuffer> getInfo(Long classId, Long teacherId, Long studentId, Integer count) {
        List<ArrangeBo> info = new ArrayList<>();
        //根据不同的id获取排课记录信息
        if (teacherId != null) {
            //教师
            info = ninArrangeMapper.getInfo(null, null, teacherId);
        } else if (classId != null) {
            NinClass ninClass = ninClassMapper.selectById(classId);
            if (ninClass.getCareerId() == 0) {
                //如果是选修班级
                info = ninArrangeMapper.getInfo(Collections.singletonList(classId), null, null);
            } else {
                List<Long> teachClassIdList = ninTeachClassMapper.getBatchTeachClassIdList(Collections.singletonList(classId));
                info = ninArrangeMapper.getInfo(null, teachClassIdList, null);
            }
        } else if (studentId != null) {
            //学生
            //获取学生选课记录
            List<NinStudentCourse> ninStudentCourses = ninStudentCourseMapper.selectList(new LambdaQueryWrapper<NinStudentCourse>()
                    .select(NinStudentCourse::getTakeClassId).eq(NinStudentCourse::getStudentId, studentId));

            List<Long> classIdList = ninStudentCourses.stream().map(NinStudentCourse::getTakeClassId).collect(Collectors.toList());

            //获取学生的行政班id
            NinStudent ninStudent = ninStudentMapper.selectById(studentId);
            //根据行政班id获取教学班id列表
            List<Long> teachClassIdList = ninTeachClassMapper.getBatchTeachClassIdList(Collections.singletonList(ninStudent.getClassId()));

            if (classIdList.isEmpty())
                classIdList = null;
            if (teachClassIdList.isEmpty())
                teachClassIdList = null;

            info = ninArrangeMapper.getInfo(classIdList, teachClassIdList, null);
        }

        //存放最终结果  星期一第二节（"12"） -> 信息字符串
        HashMap<String, StringBuffer> hashMap = new HashMap<>();

        for (ArrangeBo bo : info) {
            StringBuffer str = new StringBuffer();
            if (count != null && count != 0) {
                //单双周
                if (count % 2 == 0 && bo.getWeekly() == 1)//课程记录为单周，但count为双，跳过
                    continue;
                if (count % 2 == 1 && bo.getWeekly() == 2)//课程记录为双周，但count为单，跳过
                    continue;
                //count在开始结束范围内
                if (bo.getEndTime() < count)
                    continue;
                if (bo.getStartTime() > count)
                    continue;
            } else {
                str.append(bo.getStartTime()).append("-").append(bo.getEndTime()).append("周");
                if (bo.getWeekly() == 1) {
                    str.append("(单)/");
                } else if (bo.getWeekly() == 2) {
                    str.append("(双)/");
                } else {
                    str.append("/");
                }
            }
            String key = "" + bo.getWeek() + bo.getPitchNum();
            StringBuffer value = new StringBuffer();
            value.append(bo.getCourseName()).append("/").append(str).append(bo.getHouseName()).append("/")
                    .append(bo.getTeacherName()).append("/").append(bo.getClassName()).append("/")
                    .append(bo.getMust() == 1 ? "必修" : "选修").append(bo.getCareerId() == -1 ? "(补课)" : "");

            //同一节要上两种课(选修和必修或单双周)
            if (hashMap.get(key) == null) {
                hashMap.put(key, value);
            } else {
                hashMap.put(key, hashMap.get(key).append("##").append(value));
            }
        }

        return hashMap;
    }

    @Override
    public void exportCourseForm(String type, Long id, Integer count, HttpServletRequest request, HttpServletResponse response) {
        Map<String, StringBuffer> info = null;
        String name = null;
        if (!(count > 0 && count <= 20)) {
            count = null;
        }
        //根据type和id获取排课记录列表和id代表的角色名称
        if (type.equals(UserTypeEnum.CLAZZ.getName())) {
            name = ninClassMapper.selectById(id).getClassName();
            info = getInfo(id, null, null, count);
        } else if (type.equals(UserTypeEnum.TEACHER.getName())) {
            name = ninTeacherMapper.selectById(id).getTeacherName();
            info = getInfo(null, id, null, count);
        } else if (type.equals(UserTypeEnum.STUDENT.getName())) {
            name = ninStudentMapper.selectById(id).getStudentName();
            info = getInfo(null, null, id, count);
        }
        int len = 8;
        //表头名称，值代表的变量名
        String[] headers = new String[len], fields = new String[len];

        for (int i = 0; i < 8; i++) {
            headers[i] = CnUtil.cnWeek(i);//{"", "星期一", "星期二", ..}
            fields[i] = "" + i;//{"pitchNum", "1", "2", ..}
        }
        fields[0] = "pitchNum";

        //获得并拼接数据
        JSONArray array = new JSONArray();
        for (int i = 0; i < ApplicationConstant.DAY_PITCH_NUM; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pitchNum", CnUtil.cnPitchNum(i + 1));
            array.add(jsonObject);
        }
        for (Map.Entry<String, StringBuffer> map : info.entrySet()) {
            String[] split = map.getKey().split("");
            int i = Integer.valueOf(split[0]).intValue();
            int j = Integer.valueOf(split[1]).intValue();
            ((JSONObject) array.get(j - 1)).put("" + i, map.getValue());
        }

        String fileName;
        if (count != null) {
            fileName = name + "-第" + CnUtil.cnNum(count) + "周课程表.xls";
        } else {
            fileName = name + "-学期课程表.xls";
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        try {
            response.setHeader("Content-Disposition", /*"attachment;filename=" + */URLEncoder.encode(fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            response.flushBuffer();
            ExcelUtils.exportJsonArrayToExcel(array, fileName, response.getOutputStream(), headers, fields, "");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public List<String> getHouseApplyTime(HouseApplyBo bo) {
        List<Long> classIdList = JSON.parseArray(bo.getClassIdList(), Long.class);
        Long houseId = bo.getHouseId();
        Long teacherId = bo.getTeacherId();
        Long courseId = bo.getCourseId(); //-1为其他用途
        List<Integer> weeklyList = JSON.parseArray(bo.getWeeklyList(), Integer.class);
        weeklyList = weeklyList.stream().sorted(Comparator.comparing(i -> i)).collect(Collectors.toList());
        List<Integer> weekList = JSON.parseArray(bo.getWeekList(), Integer.class);
        weekList = weekList.stream().sorted(Comparator.comparing(i -> i)).collect(Collectors.toList());

        List<String> arrayList = new ArrayList<>();
        int len = weeklyList.size();
        int len1 = weekList.isEmpty() ? 7 : weekList.size();
        for (int i = 0; i < len; i++) {
            int weekly = weeklyList.get(i);
            for (int j = 0; j < len1; j++) {
                int week = weekList.get(j);
                for (int k = 1; k <= ApplicationConstant.DAY_PITCH_NUM; k++) {
                    arrayList.add(weekly + "#" + week + "#" + k);
                }
            }
        }


        List<Long> teachClassIdList = ninTeachClassMapper.getBatchTeachClassIdList(classIdList);

        LambdaQueryWrapper<NinArrange> wrapper = new QueryWrapper<NinArrange>().select("DISTINCT weekly, start_time, end_time, week, pitch_num").lambda();
        wrapper.in(!teachClassIdList.isEmpty(), NinArrange::getTeachClassId, teachClassIdList)
                .or().eq(NinArrange::getHouseId, houseId)
                .or().eq(NinArrange::getTeacherId, teacherId);
        List<NinArrange> ninArrangeList = ninArrangeMapper.selectList(wrapper);

        Map<Integer, Map<Integer, List<NinArrange>>> map = ninArrangeList.stream().collect(Collectors.groupingBy(NinArrange::getPitchNum, Collectors.groupingBy(NinArrange::getWeek)));

        for (Map.Entry<Integer, Map<Integer, List<NinArrange>>> map1: map.entrySet()){
            Integer key = map1.getKey();
            Map<Integer, List<NinArrange>> value = map1.getValue();
            for (Map.Entry<Integer, List<NinArrange>> map2 : value.entrySet()) {
                Integer key1 = map2.getKey();
                List<NinArrange> value1 = map2.getValue();

                HashSet<Integer> set = new HashSet<>();
                for (NinArrange arrange : value1) {
                    Integer startTime = arrange.getStartTime();
                    Integer endTime = arrange.getEndTime();
                    Integer weekly = arrange.getWeekly();
                    int sign = weekly == 0 ? 1 : 2;
                    for (int i = startTime; i <= endTime; i+=sign) {
                        set.add(i);
                    }
                }

                weeklyList.stream().filter(set::contains).forEach(i -> {
                    String str = i + "#" + key1 + "#" + key;
                    arrayList.remove(str);
                });

            }

        }

        return arrayList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultModel submitApply(HouseApplyBo bo) {

        NinArrange arrange = getOne(new LambdaQueryWrapper<NinArrange>()
                .eq(NinArrange::getMust, CourseTypeEnum.REQUIRED_COURSE.getCode()), false);
        if (null == arrange) {
            throw new ServiceException(412, "此功能将在排课完成后开放");
        }

        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(bo);

        List<Long> classIds = JSON.parseArray(bo.getClassIdList(), Long.class);
        List<NinClass> ninClasses = ninClassMapper.selectList(new LambdaQueryWrapper<NinClass>()
                .select(NinClass::getClassName).in(NinClass::getId, classIds));
        List<String> classNameList = ninClasses.stream().map(NinClass::getClassName).collect(Collectors.toList());

        jsonObject.put("className", StringUtils.join(classNameList, ","));
        NinHouse house = ninHouseService.getById(bo.getHouseId());
        jsonObject.put("houseName", house.getHouseName());
        NinTeacher ninTeacher = ninTeacherMapper.selectById(bo.getTeacherId());
        jsonObject.put("teacherName", ninTeacher.getTeacherName());
        Long courseId = bo.getCourseId();
        if (courseId == -1) {
            jsonObject.put("courseName", "其他用途");
        } else {
            NinCourse course = ninCourseMapper.selectById(courseId);
            jsonObject.put("courseName", "用于课程[" + course.getCourseName() + "]补课");
        }

        String userType = UserUtil.getUserInfo().getUserType();
        NinMessage ninMessage = new NinMessage();
        if (userType.equals(UserTypeEnum.TEACHER.getName())) {
            ninMessage.setMsg(jsonObject.toJSONString());
            ninMessage.setUserId(ApplicationConstant.ADMIN_ID);
            ninMessage.setIsConsent(0);
        } else if (userType.equals(UserTypeEnum.ADMIN.getName())) {
            //管理员直接通过申请
            addArrange(jsonObject);
            ninMessage.setUserId(jsonObject.getLong("teacherId")); //教师id

            Integer weekly = jsonObject.getInteger("weekly");
            Integer week = jsonObject.getInteger("week");
            Integer pitchNum = jsonObject.getInteger("pitchNum");
            String msg = String.format(MsgEnum.ADMIN_APPLY.getMsg(), jsonObject.get("houseName"),
                    jsonObject.get("className"), CnUtil.cnNum(weekly), CnUtil.cnWeek(week),
                    CnUtil.cnNum(pitchNum), jsonObject.get("courseName"));
            ninMessage.setMsg(msg);
        }
        ninMessageMapper.insert(ninMessage);

        //生成一条消息记录
        return ResultModel.ok();
    }

    @Override
    public int addArrange(JSONObject jsonObject) {
        String classIdList = jsonObject.getString("classIdList");
        Long courseId = jsonObject.getLong("courseId");
        Long teacherId = jsonObject.getLong("teacherId");
        Long houseId = jsonObject.getLong("houseId");
        Integer week = jsonObject.getInteger("week");
        Integer weekly = jsonObject.getInteger("weekly");
        Integer pitchNum = jsonObject.getInteger("pitchNum");

        List<Long> classIds = JSON.parseArray(classIdList, Long.class);
        if (classIds == null || classIds.size() == 0) {
            throw new ServiceException(412, "班级为空");
        }

        //判断
        Integer arrangeVerify = ninArrangeMapper.getArrangeVerify(classIds, weekly, week, pitchNum, teacherId, houseId);
        if (arrangeVerify > 0) {
            throw new ServiceException(412, "时间冲突，添加失败");
        }

        //添加教学班记录
        Long teachClassId = IDUtil.getID();
        ArrayList<NinTeachClass> ninTeachClasses = new ArrayList<>();
        List<NinClass> ninClasses = ninClassMapper.selectList(new LambdaQueryWrapper<NinClass>()
                .select(NinClass::getId, NinClass::getClassName).in(NinClass::getId, classIds));
        Map<Long, String> classMap = ninClasses.stream().collect(Collectors.toMap(NinClass::getId, NinClass::getClassName));
        for (Long classId : classIds) {
            NinTeachClass ninTeachClass = new NinTeachClass();
            ninTeachClass.setTeachClassId(teachClassId);
            ninTeachClass.setClassId(classId);
            ninTeachClass.setClassName(classMap.get(classId));
            ninTeachClasses.add(ninTeachClass);
        }
        ninTeachClassMapper.addBatch(ninTeachClasses);

        //添加排课记录
        NinArrange arrange = new NinArrange();
        arrange.setCareerId(-1L);
        arrange.setTeachClassId(teachClassId);
        arrange.setTeacherId(teacherId);
        arrange.setHouseId(houseId);
        arrange.setCourseId(courseId);
        arrange.setWeekly(WeeklyTypeEnum.WEEKLY.getCode());
        arrange.setWeek(week);
        arrange.setPitchNum(pitchNum);
        arrange.setStartTime(weekly);
        arrange.setEndTime(weekly);
        arrange.setPeopleNum(classIds.size() * ApplicationConstant.CLASS_PEOPLE_NUM);
        arrange.setMust(courseId != -1 ? ninCourseMapper.selectById(courseId).getMust() : 1);
        return ninArrangeMapper.insert(arrange);
    }
}

