package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.*;
import cn.netinnet.coursearrange.entity.bo.ArrangeBo;
import cn.netinnet.coursearrange.mapper.*;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@Service
public class NinArrangeServiceImpl extends ServiceImpl<NinArrangeMapper, NinArrange> implements INinArrangeService {

    @Autowired
    private NinArrangeMapper ninArrangeMapper;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private NinHouseMapper ninHouseMapper;
    @Autowired
    private NinStudentMapper ninStudentMapper;
    @Autowired
    private NinClassCourseMapper ninClassCourseMapper;
    @Autowired
    private NinTeacherCourseMapper ninTeacherCourseMapper;
    @Autowired
    private NinStudentCourseMapper ninStudentCourseMapper;


    @Override
    public void arrange() {
        //todo 修改数据库arrange
        // classes表（存储一起上课的班级）
        // arrange 新增classes_id,(当classId为空时，查找classes表)
        // weekly去除，新增开始周次和结束周次、单双周属性
        // 排课8课时最后，先选修后必修

        System.out.println("----开始排课----");
        long oldData = System.currentTimeMillis();

        //todo 生成前清空
        ninArrangeMapper.delete(new QueryWrapper<>());

        ArrayList<ArrangeBo> arrangeBoList = new ArrayList<>();
        ArrayList<ArrangeBo> arrangeBos = new ArrayList<>();
        List<NinClass> ninClassList = ninClassMapper.selectList(new QueryWrapper<>()).stream().sorted(Comparator.comparing(NinClass::getClassName)).collect(Collectors.toList());
        List<NinCourse> ninCourseList = ninCourseMapper.selectList(new QueryWrapper<>()).stream().sorted(Comparator.comparing(NinCourse::getMust).thenComparing(NinCourse::getNum, Comparator.reverseOrder())).collect(Collectors.toList());
        List<NinHouse> ninHouseList = ninHouseMapper.selectList(new QueryWrapper<>());
        List<NinClassCourse> ninClassCourseList = ninClassCourseMapper.selectList(new QueryWrapper<>());
        List<NinTeacherCourse> ninTeacherCourseList = ninTeacherCourseMapper.selectList(new QueryWrapper<>());


        //todo 不同教室的最大人数
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        hashMap.put(0, 150);//0对应普通教室，最多容纳150人，且必修班级一班最多50人
        hashMap.put(1, 100);
        hashMap.put(2, 50);

        //班级id, 班级
        Map<Long, NinClass> classIdMap = ninClassList.stream().collect(Collectors.toMap(NinClass::getId, Function.identity()));
        //课程id, 班级-课程列表
        Map<Long, List<NinClassCourse>> courseClassMap = ninClassCourseList.stream().collect(Collectors.groupingBy(NinClassCourse::getCourseId));
        //课程id, 教师-课程列表
        Map<Long, List<NinTeacherCourse>> courseTeacherMap = ninTeacherCourseList.stream().collect(Collectors.groupingBy(NinTeacherCourse::getCourseId));

        //遍历课程(班级-课程-教师对应)
        for (NinCourse ninCourse : ninCourseList) {
            System.out.println("----" + ninCourse.getCourseName() + " 课程开始排课----");
            //课程id
            Long courseId = ninCourse.getId();

            if (ninCourse.getMust() == 0) {
                //如果是选修
                //课程id得到班级-课程表和教师-课程表
                List<NinClassCourse> ninClassCourses = courseClassMap.get(ninCourse.getId());
                List<NinTeacherCourse> ninTeacherCourses = courseTeacherMap.get(ninCourse.getId());
                if (ninClassCourses == null) {
                    continue;//没有班级上这门课，结束
                }
                if (ninTeacherCourses == null) {
                    //有班级，但没有教师上这门课，写入bo后，结束
                    for (NinClassCourse ncc : ninClassCourses) {
                        NinClass ninClass = classIdMap.get(ncc.getClassId());
                        ArrangeBo bo = new ArrangeBo();
                        bo.setCareer("#");
                        bo.setCourseId(courseId);
                        bo.setHouseType(ninCourse.getHouseType());
                        bo.setNum(ninCourse.getNum());
                        bo.setClassId(ninClass.getId());
                        bo.setPeopleNum(ninClass.getPeopleNum());
                        bo.setMust(0);
                        arrangeBoList.add(bo);
                    }
                    continue;
                }

                //班级，教师数量
                int classSize = ninClassCourses.size();
                int teacherSize = ninTeacherCourses.size();

                //教师-班级-课程确定
                for (int i = 0, j = 0; i < classSize; i++) {
                    if (j >= teacherSize) {
                        j = 0;
                    }
                    Long classId = ninClassCourses.get(i).getClassId();
                    Long teacherId = ninTeacherCourses.get(j++).getTeacherId();
                    ArrangeBo bo = new ArrangeBo();
                    bo.setCareer("#");
                    bo.setCourseId(courseId);
                    bo.setHouseType(ninCourse.getHouseType());
                    bo.setClassId(classId);
                    bo.setPeopleNum(classIdMap.get(classId).getPeopleNum());
                    bo.setTeacherId(teacherId);
                    bo.setNum(ninCourse.getNum());
                    bo.setMust(0);
                    arrangeBoList.add(bo);
                }
            } else {
                //如果是必修
                //有上该课程的班级-课程记录
                List<NinClassCourse> ninClassCourses = courseClassMap.get(ninCourse.getId());
                List<NinTeacherCourse> ninTeacherCourses = courseTeacherMap.get(ninCourse.getId());
                if (ninClassCourses == null) {
                    continue;//没有班级上这门课，结束
                }


                //上该课程的班级id列表
                List<Long> classIdList = ninClassCourses.stream().map(NinClassCourse::getClassId).collect(Collectors.toList());
                ArrayList<NinClass> ninClasses = new ArrayList<>();
                //遍历班级id列表，获得班级列表
                for (Long classId : classIdList) {
                    ninClasses.add(classIdMap.get(classId));
                }
                //按专业划分
                Map<String, List<NinClass>> careerMap = ninClasses.stream().collect(Collectors.groupingBy(NinClass::getCareer));

                ArrayList<ArrangeBo> arrangeBos1 = new ArrayList<>();
                //最多可以几个班级一起上
                int i = hashMap.get(ninCourse.getHouseType()) / 50;

                //同专业的多个班级一起上课
                for (Map.Entry<String, List<NinClass>> e : careerMap.entrySet()) {
                    ArrangeBo bo = new ArrangeBo();
                    bo.setCareer(e.getKey());
                    bo.setCourseId(courseId);
                    bo.setHouseType(ninCourse.getHouseType());
                    bo.setNum(ninCourse.getNum());
                    bo.setMust(1);
                    //专业下的班级只有一个，或该课程最多只能一个班级（实验室）在一个教室里上课
                    if (e.getValue().size() == 1 || i == 1) {
                        for (NinClass ninClass : e.getValue()) {
                            ArrangeBo bo1 = new ArrangeBo();
                            BeanUtils.copyProperties(bo, bo1);
                            bo1.setClassId(ninClass.getId());
                            bo1.setPeopleNum(ninClass.getPeopleNum());
                            arrangeBos1.add(bo1);
                        }
                    } else if (i == 2) {
                        //该专业下班级不止一个，但该课程同时最多只能两个班级上课
                        List<NinClass> value = e.getValue();
                        int a = 0;
                        //如果余2等于1，即分班时，(..2,2,1)
                        if (value.size() % 2 == 1) {
                            a = -1;
                        }
                        for (int j = 0; j < value.size() + a; j += 2) {
                            ArrangeBo bo1 = new ArrangeBo();
                            BeanUtils.copyProperties(bo, bo1);
                            ArrayList<Long> longs = new ArrayList<>(2);
                            longs.add(value.get(j).getId());
                            longs.add(value.get(j + 1).getId());
                            bo1.setClassIdList(longs);
                            bo1.setPeopleNum(value.get(j).getPeopleNum() + value.get(j + 1).getPeopleNum());
                            arrangeBos1.add(bo1);
                        }
                        if (a == -1) {
                            ArrangeBo bo1 = new ArrangeBo();
                            BeanUtils.copyProperties(bo, bo1);
                            bo1.setClassId(value.get(value.size() - 1).getId());
                            bo1.setPeopleNum(value.get(value.size() - 1).getPeopleNum());
                            arrangeBos1.add(bo1);
                        }
                    } else if (i == 3) {
                        //班级不止一个，但同时最多可以有三个班级上课
                        List<NinClass> value = e.getValue();
                        //不会出现一个班级的情况
                        int a = 0;//做标记，余0，(..3,3)
                        if (value.size() % 3 == 1) {
                            a = -4;//余1，(..3,3,2,2)
                        } else if (value.size() % 3 == 2) {
                            a = -2;//余2，(..3,3,2)
                        }
                        for (int j = 0; j < value.size() + a; j += 3) {
                            ArrangeBo bo1 = new ArrangeBo();
                            BeanUtils.copyProperties(bo, bo1);
                            ArrayList<Long> longs = new ArrayList<>(3);
                            longs.add(value.get(j).getId());
                            longs.add(value.get(j + 1).getId());
                            longs.add(value.get(j + 2).getId());
                            bo1.setClassIdList(longs);
                            bo1.setPeopleNum(value.get(j).getPeopleNum() + value.get(j + 1).getPeopleNum() + value.get(j + 2).getPeopleNum());
                            arrangeBos1.add(bo1);
                        }
                        for (int j = a; j < 0; j += 2) {
                            ArrangeBo bo1 = new ArrangeBo();
                            BeanUtils.copyProperties(bo, bo1);
                            ArrayList<Long> longs = new ArrayList<>(2);
                            longs.add(value.get(value.size() + j).getId());
                            longs.add(value.get(value.size() + j + 1).getId());
                            bo1.setClassIdList(longs);
                            bo1.setPeopleNum(value.get(value.size() + j).getPeopleNum() + value.get(value.size() + j + 1).getPeopleNum());
                            arrangeBos1.add(bo1);
                        }
                    }
                }

                //没有教该门课的教师
                if (ninTeacherCourses == null) {
                    arrangeBoList.addAll(arrangeBos1);
                    continue;
                }

                //确定课程-班级-教师
                int teacherSize = ninTeacherCourses.size();
                for (int j = 0, k = 0; j < arrangeBos1.size(); j++) {
                    if (k >= teacherSize) {
                        k = 0;
                    }
                    Long teacherId = ninTeacherCourses.get(k++).getTeacherId();
                    arrangeBos1.get(j).setTeacherId(teacherId);
                }
                arrangeBoList.addAll(arrangeBos1);
            }
        }

        //以类型对教室分组
        Map<Integer, List<NinHouse>> collect = ninHouseList.stream().collect(Collectors.groupingBy(NinHouse::getHouseType));
        //在确定班级-课程-教师的情况下，确定教室
        for (int i = 0; i < arrangeBoList.size(); i++) {
            ArrangeBo bo = arrangeBoList.get(i);
            //符合教室类型的教室
            List<NinHouse> ninHouses = collect.get(bo.getHouseType());
            //座位大于人数
            List<NinHouse> collect1 = ninHouses.stream().filter(h -> h.getSeat() >= bo.getPeopleNum()).collect(Collectors.toList());
            //随机选一个符合条件的教室
            if (collect1 != null && collect1.size() != 0) {
                int index = (int) (Math.random() * collect1.size());
                arrangeBoList.get(i).setHouseId(collect1.get(index).getId());
            }
        }


        //遍历arrangeBoList，
        for (ArrangeBo bo : arrangeBoList) {
            List<int[]> time = time(arrangeBos, bo);
            //返回了null，即提前指定的教师、教室安排不下，
            //重新更换教师、教室（遍历，成功则跳出，否则保持null）
            ok:
            for (int[] t : time) {
                if (t == null) {
                    //符合条件的教师列表
                    List<NinTeacherCourse> ninTeacherCourses = courseTeacherMap.get(bo.getCourseId());
                    //符合的教室列表
                    List<NinHouse> ninHouses = collect.get(bo.getHouseType()).stream().filter(h -> h.getSeat() >= bo.getPeopleNum()).collect(Collectors.toList());
                    for (NinTeacherCourse ntc : ninTeacherCourses) {
                        ok1:
                        for (NinHouse h : ninHouses) {
                            bo.setTeacherId(ntc.getTeacherId());
                            bo.setHouseId(h.getId());
                            List<int[]> time1 = time(arrangeBos, bo);
                            for (int[] t1 : time1) {
                                if (t1 == null) {
                                    break ok1;
                                }
                            }
                            time = time1;
                            break ok;
                        }
                    }
                    break ok;
                }
            }
            //time是时间段，返回 time.size()*classIdList.size() 长度的
            List<ArrangeBo> separate = separate(bo, time);
            arrangeBos.addAll(separate);
        }

        ArrayList<NinArrange> ninArrangeList = new ArrayList<>();

        for (ArrangeBo bo : arrangeBos) {
            int count = 8;
            Integer weekly = bo.getWeekly();
            //bo转NinArrange
            if (bo.getNum() == 8) {
                count = 2;
                weekly = bo.getWeekly_8();
            }
//            因为是数组下标，时间要+1
            for (int i = 0; i < count; i++) {
                NinArrange ninArrange = new NinArrange();
                ninArrange.setId(IDUtil.getID());
                ninArrange.setModifyUserId(UserUtil.getUserInfo().getUserId());
                ninArrange.setCreateUserId(UserUtil.getUserInfo().getUserId());
                ninArrange.setClassId(bo.getClassId());
                ninArrange.setCourseId(bo.getCourseId());
                ninArrange.setTeacherId(bo.getTeacherId());
                ninArrange.setHouseId(bo.getHouseId());
                ninArrange.setMust(bo.getMust());
                if (weekly != null) {
                    ninArrange.setWeekly(weekly + 1 + 2 * i);
                    ninArrange.setWeek(bo.getWeek() + 1);
                    ninArrange.setPitchNum(bo.getPitchNum() + 1);
                }
                ninArrangeList.add(ninArrange);
                if (weekly == null) {
                    break;
                }
            }
        }

        int len = ninArrangeList.size();

        for (int i = 0; i < len; i += 500) {
            if (i + 500 > len) {
                ninArrangeMapper.addBatch(ninArrangeList.subList(i, len));
            } else {
                ninArrangeMapper.addBatch(ninArrangeList.subList(i, i + 500));
            }
        }
        long newData = System.currentTimeMillis();
        System.out.println("----排课结束,用时" + (newData - oldData) + "毫秒----");
    }

    @Override
    public Map<String, String> getInfo(Long classId, Long teacherId, Long studentId, Integer weekly) {
        List<Map<String, Object>> info = new ArrayList<>();
        if (studentId != null) {
            List<Long> classIdList = new ArrayList<>();
            //学生-课程，获取学生的选修教学班有哪些
            List<NinStudentCourse> ninStudentCourses = ninStudentCourseMapper.selectList(new QueryWrapper<>(new NinStudentCourse() {{
                setStudentId(studentId);
            }}));
            if (ninStudentCourses != null && ninStudentCourses.size() != 0) {
                for (NinStudentCourse nsc : ninStudentCourses) {
                    classIdList.add(nsc.getTakeClassId());
                }
            }
            //获取学生的班级记录
            NinStudent ninStudent = ninStudentMapper.selectById(studentId);
            classIdList.add(ninStudent.getClassId());
            //多个班级查询
            info = ninArrangeMapper.getInfo(classIdList, null, null, weekly);
        } else {
            info = ninArrangeMapper.getInfo(null, classId, teacherId, weekly);
        }

        HashMap<String, String> hashMap = new HashMap<>();
        if (weekly == null) {
            Map<Integer, Map<Integer, List<Map<String, Object>>>> collect = info.stream().sorted(Comparator.comparing((Map<String, Object> map) -> (Integer) map.get("weekly"))).collect(Collectors.groupingBy(i -> (Integer) i.get("week"), Collectors.groupingBy(i -> (Integer) i.get("pitchNum"))));
            for (Map.Entry<Integer, Map<Integer, List<Map<String, Object>>>> e : collect.entrySet()) {
                for (Map.Entry<Integer, List<Map<String, Object>>> e1 : e.getValue().entrySet()) {
                    List<Map<String, Object>> value = e1.getValue();
                    int startWeekly = (int) value.get(0).get("weekly");
                    int size = value.size();
                    String str = "";
                    //todo 教师课程表会有问题

                    if (size == 16) {
                        str = "1-16周";
                    } else if (size == 8) {
                        str = startWeekly + "-" + (startWeekly + 14) + "周（" + (startWeekly == 1 ? "单周" : "双周") + "）";
                    } else if (size == 4) {
                        str = startWeekly + "-" + (startWeekly + 3) + "周";
                    }

                    Map<String, Object> m = value.get(0);
                    String key = ""+e.getKey()+e1.getKey();
                    if (hashMap.get(key) != null){
                        hashMap.put(key, hashMap.get(key) + "/" + m.get("className") + "/" + m.get("teacherName") + "/" + m.get("courseName") + "/" + m.get("houseName") + "/" + str + "/" + ((int) m.get("must") == 0 ? "选修" : "必修"));
                    } else {
                        hashMap.put(key, m.get("className") + "/" + m.get("teacherName") + "/" + m.get("courseName") + "/" + m.get("houseName") + "/" + str + "/" + ((int) m.get("must") == 0 ? "选修" : "必修"));
                    }
                }
            }
        } else {
            for (Map<String, Object> m : info) {
                hashMap.put("" + m.get("week") + m.get("pitchNum"), m.get("className") + "/" + m.get("teacherName") + "/" + m.get("courseName") + "/" + m.get("houseName") + "/" + ((int) m.get("must") == 0 ? "选修" : "必修"));
            }
        }
        return hashMap;
    }






    //时间*班级拆开(时间只有前两周)
    public List<ArrangeBo> separate(ArrangeBo bo, List<int[]> time) {
        List<Long> classIdList = bo.getClassIdList();
        ArrayList<ArrangeBo> arrangeBos = new ArrayList<>();
        Integer weekly_8 = 0;
        if (bo.getNum() == 8) {
            weekly_8 = (int) (Math.random() * 12);
        }
        for (int[] t : time) {
            if (t != null) {
                bo.setWeekly(t[0]);
                bo.setWeek(t[1]);
                bo.setPitchNum(t[2]);
                bo.setWeekly_8(weekly_8++);
            }

            if (classIdList != null && classIdList.size() != 0) {
                for (Long classId : classIdList) {
                    ArrangeBo bo1 = new ArrangeBo();
                    BeanUtils.copyProperties(bo, bo1);
                    bo1.setClassId(classId);
                    arrangeBos.add(bo1);
                }
            } else {
                ArrangeBo bo1 = new ArrangeBo();
                BeanUtils.copyProperties(bo, bo1);
                bo1.setClassId(bo.getClassId());
                arrangeBos.add(bo1);
            }
        }
        return arrangeBos;
    }

    //生成时间表对应
    public List<int[]> time(List<ArrangeBo> arrangeBoList, ArrangeBo bo) {
        //课时
        int num = bo.getNum();
        //选修
        int must = bo.getMust();

        List<int[]> list = new ArrayList<>(4);

        int[][][] time = new int[2][7][5];

        List<Long> classIdList = bo.getClassIdList();
        Long teacherId = bo.getTeacherId();
        Long houseId = bo.getHouseId();
        Long classId = bo.getClassId();

        List<ArrangeBo> arranges = null;

        //必修选修限制（选修只在周四上）
        if (must == 0) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 5; j++) {
                    if (j == 3) {
                        continue;
                    }
                    for (int k = 0; k < 5; k++) {
                        time[i][j][k] = 1;
                    }
                }
            }
        }

        //不空闲的时间，教师（可能为空）和教室
        Stream<ArrangeBo> ninArrangeStream = arrangeBoList.stream();

        if (classIdList != null && classIdList.size() != 0) {
            //多班级
            int size = classIdList.size();
            if (size == 2) {
                //两个
                arranges = ninArrangeStream.filter(i -> (i.getTeacherId() != null && teacherId != null ? i.getTeacherId() == teacherId : true) || i.getHouseId() != houseId || i.getClassId() == classIdList.get(0) || i.getClassId() == classIdList.get(1)).collect(Collectors.toList());
            } else if (size == 3) {
                //三个
                arranges = ninArrangeStream.filter(i -> (i.getTeacherId() != null && teacherId != null ? i.getTeacherId() == teacherId : true) || i.getHouseId() != houseId || i.getClassId() == classIdList.get(0) || i.getClassId() == classIdList.get(1) || i.getClassId() == classIdList.get(2)).collect(Collectors.toList());
            }
        } else {
            //一个班级
            arranges = ninArrangeStream.filter(i -> (i.getTeacherId() != null && teacherId != null ? i.getTeacherId() == teacherId : true) || i.getHouseId() != houseId || i.getClassId() == classId).collect(Collectors.toList());
        }

        //三维数组，周次（单双周，记2），星期（7），节数（5）
        //1不能排，0可以排
        for (ArrangeBo arrange : arranges) {
            if (arrange.getWeekly() != null) {
                time[arrange.getWeekly()][arrange.getWeek()][arrange.getPitchNum()] = 1;
            }
        }


        int week_ = 8;
        //按课时，生成时间段
        if (num == 64) {
            for (int i = 0; i < 2; i++) {
                List<int[]> two = two(time, week_);
                if (two.get(0) != null) {
                    week_ = two.get(0)[1];
                }
                list.addAll(two);
            }
        } else if (num == 32 || num == 8) {
            List<int[]> two = two(time, week_);
            list.addAll(two);
        } else if (num == 16) {
            List<int[]> one = one(time);
            list.addAll(one);
        } else if (num == 48) {
            List<int[]> one = one(time);
            if (one.get(0) != null) {
                week_ = one.get(0)[1];
            }
            List<int[]> two = two(time, week_);
            list.addAll(one);
            list.addAll(two);
        }
        return list;
    }

    //单双周
    public List<int[]> one(int[][][] time) {
        List<int[]> list = new ArrayList();
        int weekly;
        int week;
        int pitchNum;
        int count = 0;
        ok:
        while (true) {
            if (count++ < 50) {
                weekly = (int) (Math.random() * 2);
                week = (int) (Math.random() * 5);
                pitchNum = (int) (Math.random() * 5);
                if (time[weekly][week][pitchNum] == 0) {
                    time[weekly][week][pitchNum] = 1;
                    int[] l1 = {weekly, week, pitchNum};
                    list.add(l1);
                    break;
                }
            } else {
                for (int i = 0; i < 2; i++) {
                    for (int j = 6; j >= 0; j--) {
                        for (int k = 0; k < 5; k++) {
                            if (time[i][j][k] == 0) {
                                time[i][j][k] = 1;
                                int[] l1 = {i, j, k};
                                list.add(l1);
                                break ok;
                            }
                        }
                    }
                }
                list.add(null);
                break;
            }
        }
        return list;
    }

    //每周
    public List<int[]> two(int[][][] time, int week_) {
        List<int[]> list = new ArrayList();
        int week;
        int pitchNum;
        int count = 0;
        ok:
        while (true) {
            if (count++ < 25) {
                week = (int) (Math.random() * 5);
                if (week == week_) {
                    continue;
                }
                pitchNum = (int) (Math.random() * 5);
                if (time[0][week][pitchNum] == 0 && time[1][week][pitchNum] == 0) {
                    time[0][week][pitchNum] = 1;
                    time[1][week][pitchNum] = 1;
                    int[] l1 = {0, week, pitchNum};
                    int[] l2 = {1, week, pitchNum};
                    list.add(l1);
                    list.add(l2);
                    break;
                }
            } else {
                for (int i = 6; i >= 0; i--) {
                    if (i == week_) {
                        continue;
                    }
                    for (int j = 0; j < 5; j++) {
                        if (time[0][i][j] == 0 && time[1][i][j] == 0) {
                            time[0][i][j] = 1;
                            time[1][i][j] = 1;
                            int[] l1 = {0, i, j};
                            int[] l2 = {1, i, j};
                            list.add(l1);
                            list.add(l2);
                            break ok;
                        }
                    }
                }
                list.add(null);
                list.add(null);
                break;
            }
        }
        return list;
    }

}
