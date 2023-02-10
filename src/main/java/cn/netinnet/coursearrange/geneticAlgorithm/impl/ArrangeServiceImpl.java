package cn.netinnet.coursearrange.geneticAlgorithm.impl;

import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.enums.CourseTypeEnum;
import cn.netinnet.coursearrange.enums.WeeklyTypeEnum;
import cn.netinnet.coursearrange.geneticAlgorithm.domain.Chromosome;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.geneticAlgorithm.ArrangeService;
import cn.netinnet.coursearrange.geneticAlgorithm.domain.TaskRecord;
import cn.netinnet.coursearrange.geneticAlgorithm.domain.TeaTask;
import cn.netinnet.coursearrange.util.IDUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArrangeServiceImpl implements ArrangeService {
    @Autowired
    private NinArrangeMapper ninArrangeMapper;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private NinHouseMapper ninHouseMapper;
    @Autowired
    private NinTeacherCourseMapper ninTeacherCourseMapper;
    @Autowired
    private NinCareerMapper ninCareerMapper;
    @Autowired
    private NinCareerCourseMapper ninCareerCourseMapper;

    private static Map<Integer, List<NinHouse>> houseTypeNinHouseListMap;


    private static List<TeaTask> teaTaskList;
    private static List<TaskRecord> electiveTaskRecordList;

    //生成一个解
    @Override
    public Chromosome generateChromosome() {
        //生成教学任务(课程班级教师)(只生成一次)
        generateTeaTask();
        //教学任务安排教室
        List<TaskRecord> taskRecords = selectHouse();
        //随机安排时间
        List<TaskRecord> recordList = generateTime(taskRecords);
        if (null == recordList) {
            return null;
        }
        //获取数据库中选修课数据(只生成一次)
        List<TaskRecord> electiveTaskList = getElectiveTaskList();
        recordList.addAll(electiveTaskList);
        //解决硬冲突
        verifyClashSolve(recordList);

        Chromosome chromosome = new Chromosome();
        chromosome.setTaskRecordList(recordList);
        return chromosome;
    }

    //生成教学任务(班级 + 课程 + 教师)
    public void generateTeaTask() {
        if (null != teaTaskList) {
            return;
        }

        //专业表
        List<NinCareer> ninCareerList = ninCareerMapper.selectList(new QueryWrapper<>());
        List<Long> careerIdList = ninCareerList.stream().map(NinCareer::getId).collect(Collectors.toList());

        //课程表
        List<NinCourse> ninCourseList = ninCourseMapper.selectList(new LambdaQueryWrapper<NinCourse>().eq(NinCourse::getMust, CourseTypeEnum.REQUIRED_COURSE.getCode()));
        Map<Long, NinCourse> idNinCourseMap = ninCourseList.stream().collect(Collectors.toMap(NinCourse::getId, Function.identity()));

        //<专业id，班级列表>//有顺序的
        List<NinClass> ninClassList = ninClassMapper.selectList(new QueryWrapper<>());
        Map<Long, List<NinClass>> careerIdNinClassListMap = ninClassList.stream().sorted(Comparator.comparing(NinClass::getClassName)).filter(i -> i.getCareerId() != null).collect(Collectors.groupingBy(NinClass::getCareerId));

        //专业选课表
        List<NinCareerCourse> ninCareerCourseList = ninCareerCourseMapper.selectList(new QueryWrapper<>());
        Map<Long, List<NinCareerCourse>> careerIdNinCareerCourseListMap = ninCareerCourseList.stream().collect(Collectors.groupingBy(NinCareerCourse::getCareerId));

        Map<Long, List<Long>> teaIdClassListMap = new HashMap<>();

        //教学任务列表
        List<TeaTask> taskList = new ArrayList<>();


        //遍历专业列表 确定教学班和课程
        for (Long careerId : careerIdList) {
            if (careerId == 0 || careerId == -1) {
                continue;
            }
            //获得该专业的专业-课程表
            List<NinCareerCourse> ninCareerCourses = careerIdNinCareerCourseListMap.get(careerId);
            if (null == ninCareerCourses) {
                continue;
            }

            //该专业的班级列表（有序的）
            List<NinClass> classList = careerIdNinClassListMap.get(careerId);
            if (null == classList) {
                continue;
            }

            //该专业的班级数量
            Integer classNum = classList.size();
            //<课程上课数量，Long{该专业分成的教学班id}>
            Map<Integer, Long[]> teachClassMap = new HashMap<>();

            //循环该专业的专业选课表
            for (NinCareerCourse ncc : ninCareerCourses) {
                //该专业-课程记录对应的课程
                NinCourse ninCourse = idNinCourseMap.get(ncc.getCourseId());

                //最多几个班一起上课
                Integer maxClassNum = ninCourse.getMaxClassNum();

                //如果为空，生成教学班
                if (teachClassMap.get(maxClassNum) == null) {

                    int[] ints = grouping(classNum,  maxClassNum);

                    Long[] teachClasses = new Long[ints.length];
                    for (int j = 0, count = 0; j < ints.length; j++) {
                        long teachClassId = IDUtil.getID();
                        List<Long> classes = new ArrayList<>();
                        for (int k = 0; k < ints[j]; k++, count++) {
                            classes.add(classList.get(count).getId());
                        }
                        teachClasses[j] = teachClassId;
                        teaIdClassListMap.put(teachClassId, classes);
                    }
                    teachClassMap.put(maxClassNum, teachClasses);
                }

                Long[] longs = teachClassMap.get(maxClassNum);

                for (int i = 0; i < longs.length; i++) {
                    if (longs[i] == null) {
                        break;
                    }

                    TeaTask teaTask = new TeaTask();
                    teaTask.setCareerId(careerId);
                    teaTask.setCourseId(ninCourse.getId());
                    teaTask.setTeachClassId(longs[i]);
                    List<Long> classes = teaIdClassListMap.get(longs[i]);
                    teaTask.setClassIdList(classes);
                    teaTask.setPeopleNum(classes.size() * ApplicationConstant.CLASS_PEOPLE_NUM);
                    teaTask.setHouseType(ninCourse.getHouseType());

                    double v = (double) ninCourse.getCourseTime() / 2 / ninCourse.getWeekTime();
                    if (v <= 0.5) {
                        //两周一节
                        teaTask.setCode(1);
                    } else if (v <= 1) {
                        //一周一节
                        teaTask.setCode(2);
                    } else if (v <= 1.5) {
                        //两周三节
                        teaTask.setCode(3);
                    } else {
                        //一周两节
                        teaTask.setCode(4);
                    }
                    taskList.add(teaTask);
                }
            }
        }
        teaTaskList = taskList;
        selectTeacher();
    }

    //选择教师
    public void selectTeacher() {
        Map<Long, Integer> teaCodeMap = new HashMap<>();

        //教师选课表
        List<NinTeacherCourse> ninTeacherCourseList = ninTeacherCourseMapper.selectList(new QueryWrapper<>());
        Map<Long, List<NinTeacherCourse>> courseIdNinTeacherCourseListMap = ninTeacherCourseList.stream().collect(Collectors.groupingBy(NinTeacherCourse::getCourseId));
        Map<Long, List<TeaTask>> courseIdTeaTaskListMap = teaTaskList.stream().collect(Collectors.groupingBy(TeaTask::getCourseId));

        for (Long courseId : courseIdNinTeacherCourseListMap.keySet()) {
            List<NinTeacherCourse> teacherCourseList = courseIdNinTeacherCourseListMap.get(courseId);
            List<Long> teacherIdList = teacherCourseList.stream().map(NinTeacherCourse::getTeacherId).collect(Collectors.toList());
            List<TeaTask> teaTaskList = courseIdTeaTaskListMap.get(courseId);
            if (null == teaTaskList) {
                Long teacherId = teacherCourseList.get(0).getTeacherId();
                teaCodeMap.merge(teacherId, 2, Integer::sum);
            } else {
                for (TeaTask task : teaTaskList) {
                    Long minTeacherId = teacherIdList.get(0);
                    Integer minCode = teaCodeMap.get(minTeacherId);
                    for (Long teacherId : teacherIdList) {
                        Integer code = teaCodeMap.get(teacherId);
                        if (null == code) {
                            teaCodeMap.put(teacherId, 0);
                            code = 0;
                        }
                        if (null == minCode || code < minCode) {
                            minTeacherId = teacherId;
                            minCode = code;
                        }
                    }
                    teaCodeMap.put(minTeacherId, teaCodeMap.get(minTeacherId) + task.getCode());
                    task.setTeacherId(minTeacherId);
                }
            }
        }
    }

    //选择教室
    public List<TaskRecord> selectHouse() {
        Map<Integer, List<NinHouse>> houseTypeNinHouseListMap = getHouseTypeNinHouseListMap();
        List<TaskRecord> taskRecordList = new ArrayList<>();

        for (TeaTask task : teaTaskList) {
            TaskRecord taskRecord = new TaskRecord(task);
            int houseType = task.getHouseType();
            int peopleNum = task.getPeopleNum();
            int code = task.getCode();

            List<NinHouse> ninHouses = houseTypeNinHouseListMap.get(houseType);
            List<NinHouse> ninHouseList = ninHouses.stream().filter(i -> i.getSeat() >= peopleNum).collect(Collectors.toList());
            int i = (int) (Math.random() * ninHouseList.size());

            taskRecord.setHouseId(ninHouseList.get(i).getId());
            taskRecord.setSeat(ninHouseList.get(i).getSeat());

            switch (code) {
                case 1:
                    taskRecord.setWeekly((int) (Math.random() * 2) + 1);
                    taskRecordList.add(taskRecord);
                    break;
                case 2:
                    taskRecord.setWeekly(WeeklyTypeEnum.WEEKLY.getCode());
                    taskRecordList.add(taskRecord);
                    break;
                case 3:
                    taskRecord.setWeekly(WeeklyTypeEnum.WEEKLY.getCode());
                    taskRecordList.add(taskRecord);

                    TaskRecord taskRecord1 = new TaskRecord(task);
                    taskRecord1.setHouseId(taskRecord.getHouseId());
                    taskRecord1.setSeat(taskRecord.getSeat());
                    taskRecord1.setWeekly((int) (Math.random() * 2) + 1);
                    taskRecordList.add(taskRecord1);
                    break;
                case 4:
                    taskRecord.setWeekly(WeeklyTypeEnum.WEEKLY.getCode());
                    taskRecordList.add(taskRecord);

                    TaskRecord taskRecord2 = new TaskRecord(task);
                    taskRecord2.setHouseId(taskRecord.getHouseId());
                    taskRecord2.setSeat(taskRecord.getSeat());
                    taskRecord2.setWeekly(WeeklyTypeEnum.WEEKLY.getCode());
                    taskRecordList.add(taskRecord2);
                    break;
            }

        }
        return taskRecordList;
    }

    //随机生成时间
    public List<TaskRecord> generateTime(List<TaskRecord> taskRecordList) {
        for (TaskRecord record : taskRecordList) {
            record.setWeek((int) (Math.random() * 5) + 1);
            record.setPitchNum((int) (Math.random() * 5) + 1);
        }
        return taskRecordList;
    }

    //校验所有冲突并解决
    @Override
    public int verifyClashSolve(List<TaskRecord> taskRecordList) {
        int count = 0;
        int size = taskRecordList.size();
        for (int i = 0; i <size; i++) {
            boolean b = verifyClash(taskRecordList.subList(i + 1, size), taskRecordList.get(i));
            if (!b) {
                boolean b1 = solveClash(taskRecordList, taskRecordList.get(i));
                if (!b1) {
                    count++;
                }
            }
        }
        return count;
    }

    //冲突校验
    @Override
    public boolean verifyClash(List<TaskRecord> taskRecordList, TaskRecord taskRecord) {
        for (TaskRecord r : taskRecordList) {
            //同一对象，跳过
            if (r == taskRecord) {
                continue;
            }
            //校验时间
            if (!verifyTime(r, taskRecord)) {

                //教室
                if (r.getHouseId().equals(taskRecord.getHouseId())) {
                    return false;
                }

                TeaTask teaTask1 = r.getTeaTask();
                TeaTask teaTask2 = taskRecord.getTeaTask();
                //教师
                if (teaTask1.getTeacherId().equals(teaTask2.getTeacherId())) {
                    return false;
                }

                //班级
                List<Long> classIdList1 = teaTask1.getClassIdList();
                List<Long> classIdList2 = teaTask2.getClassIdList();
                for (Long classId1 : classIdList1) {
                    for (Long classId2 : classIdList2) {
                        if (classId1.equals(classId2)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    //冲突解决
    @Override
    public boolean solveClash(List<TaskRecord> taskRecordList, TaskRecord taskRecord) {
        //遍历时间无法解决，遍历教室
        List<NinHouse> ninHouses = getHouseTypeNinHouseListMap().get(taskRecord.getTeaTask().getHouseType());
        List<NinHouse> ninHouseList = ninHouses.stream()
                .filter(i -> i.getSeat() >= taskRecord.getTeaTask().getPeopleNum())
                .collect(Collectors.toList());
        //打乱顺序，避免多次遍历教室，导致第一个教室安排过多
        Collections.shuffle(ninHouseList);
        for (NinHouse house: ninHouseList) {
            taskRecord.setHouseId(house.getId());
            boolean b1 = traversalTime(taskRecordList, taskRecord);
            if (b1) {
                return true;
            }
        }
        taskRecord.setHouseId(null);
        taskRecord.setWeek(null);
        taskRecord.setPitchNum(null);
        System.out.println("冲突无法解决");
        return false;

    }



    //遍历时间
    public boolean traversalTime(List<TaskRecord> taskRecordList, TaskRecord taskRecord) {
        int sign = (int) (Math.random() * 25), count = 0;
        while (count++ < 25) {
            if (sign == 25) {
                sign = 0;
            }
            int i = sign / 5 + 1;
            int j = sign % 5 + 1;
            taskRecord.setWeek(i);
            taskRecord.setPitchNum(j);
            boolean b = verifyClash(taskRecordList, taskRecord);
            if (b) {
                break;
            }
            sign++;
        }
        return count != 25;
    }

    //时间校验
    public boolean verifyTime(TaskRecord record1, TaskRecord record2) {
        Integer weekly1 = record1.getWeekly(), week1 = record1.getWeek(), pitchNum1 = record1.getPitchNum();
        Integer weekly2 = record2.getWeekly(), week2 = record2.getWeek(), pitchNum2 = record2.getPitchNum();
        if (null != week1 && null != week2) {
            //等于3，即一个单周，一个双周
            if (week1.equals(week2) && pitchNum1.equals(pitchNum2) && weekly1 + weekly2 != 3) {
                return false;
            }
        }
        return true;
    }

    //教室类型列表map
    public Map<Integer, List<NinHouse>> getHouseTypeNinHouseListMap() {
        if (null == houseTypeNinHouseListMap) {
            List<NinHouse> ninHouseList = ninHouseMapper.selectList(new QueryWrapper<>());
            houseTypeNinHouseListMap = ninHouseList.stream().collect(Collectors.groupingBy(NinHouse::getHouseType));
        }
        return houseTypeNinHouseListMap;
    }
    //获取数据库中选修课数据
    public List<TaskRecord> getElectiveTaskList() {
        if (null == electiveTaskRecordList) {
            List<NinArrange> ninArranges = ninArrangeMapper.selectList(new LambdaQueryWrapper<NinArrange>()
                    .eq(NinArrange::getMust, CourseTypeEnum.OPTIONAL.getCode()));
            List<TaskRecord> recordList = new ArrayList<>();
            ninArranges.forEach(arr -> {
                TeaTask teaTask = new TeaTask();
                teaTask.setClassIdList(Collections.singletonList(arr.getClassId()));
                teaTask.setCode(2);
                teaTask.setCourseId(arr.getCourseId());
                teaTask.setPeopleNum(arr.getPeopleNum());
                teaTask.setTeacherId(arr.getTeacherId());

                TaskRecord taskRecord = new TaskRecord(teaTask);
                taskRecord.setHouseId(arr.getHouseId());
                taskRecord.setWeekly(0);
                taskRecord.setWeek(arr.getWeek());
                taskRecord.setPitchNum(arr.getPitchNum());

                recordList.add(taskRecord);
            });
            electiveTaskRecordList = recordList;
        }
        return electiveTaskRecordList;
    }
    //平均分配教学班
    public int[] grouping(int total, int maxNum) {
        int len =(int) Math.ceil(total *1.0 / maxNum);
        int[] ints = new int[len];
        for (int i = 0; i < total; i++) {
            ints[i % len]++;
        }
        return ints;
    }



    @Override
    public void mutation(List<TaskRecord> taskRecordList) {
        int len = taskRecordList.size() - getElectiveTaskList().size();
        //随机产生突变时间
        int a = (int) (Math.random() * 5) + 1;
        int b = (int) (Math.random() * 5) + 1;
        while (a == b) {
            b = (int) (Math.random() * 5) + 1;
        }

        for (int i = 0; i < len; i++) {
            TaskRecord record = taskRecordList.get(i);
            if (record.getWeek() == a) {
                record.setWeek(b);
                continue;
            }
            if (record.getWeek() == b) {
                record.setWeek(a);
            }
        }
    }

    @Override
    public void genetic(List<TaskRecord> taskRecordList1, List<TaskRecord> taskRecordList2) {
        int len = taskRecordList1.size() - getElectiveTaskList().size();

        //随机产生交叉互换时间
        int a = (int) (Math.random() * len);
        int b = (int) (Math.random() * len);
        while (a == b) {
            b = (int) (Math.random() * len);
        }
        int max = Math.max(a, b);
        int min = Math.min(a, b);

        for (int i = 0; i < 2 ; i++) {

            TaskRecord taskRecord1 = taskRecordList1.get(i);
            TaskRecord taskRecord2 = taskRecordList2.get(i);

            //交换时间
            int week1 = taskRecord1.getWeek();
            int week2 = taskRecord2.getWeek();
            taskRecord1.setWeek(week2);
            taskRecord2.setWeek(week1);

            int pitchNum1 = taskRecord1.getPitchNum();
            int pitchNum2 = taskRecord2.getPitchNum();
            taskRecord1.setPitchNum(pitchNum2);
            taskRecord2.setPitchNum(pitchNum1);
        }
    }
}
