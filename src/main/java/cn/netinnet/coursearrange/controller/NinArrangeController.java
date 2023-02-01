package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.ArrangeBo;
import cn.netinnet.coursearrange.bo.HouseApplyBo;
import cn.netinnet.coursearrange.domain.UserInfo;
import cn.netinnet.coursearrange.entity.NinArrange;
import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.entity.NinCourse;
import cn.netinnet.coursearrange.entity.NinTeachClass;
import cn.netinnet.coursearrange.enums.CourseTypeEnum;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.mapper.NinClassMapper;
import cn.netinnet.coursearrange.mapper.NinCourseMapper;
import cn.netinnet.coursearrange.mapper.NinTeachClassMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.service.INinTeachClassService;
import cn.netinnet.coursearrange.service.impl.NinTeachClassServiceImpl;
import cn.netinnet.coursearrange.text.GeneticAlgorithm;
import cn.netinnet.coursearrange.text.TaskRecord;
import cn.netinnet.coursearrange.text.TeaTask;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@RestController
@RequestMapping("")
@RequiresRoles(value = {"admin", "teacher", "student"}, logical = Logical.OR)
public class NinArrangeController {
    @Autowired
    private INinArrangeService ninArrangeService;
    @Autowired
    private GeneticAlgorithm geneticAlgorithm;
    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private INinTeachClassService ninTeachClassService;
    @Autowired
    private NinClassMapper ninClassMapper;

    /*--排课--*/
    //跳转排课页面
    @GetMapping("/nin-arrange")
    public ModelAndView gotoView() {
        return new ModelAndView("view/arrangeView");
    }

    /**
     * 自动排课
     */
    @GetMapping("/nin-arrange/arrange")
    public ResultModel arrange() {
//        ninArrangeService.arrange();
        List<TaskRecord> caculte = geneticAlgorithm.caculte();
        Map<Long, NinCourse> collect = ninCourseMapper.selectList(new LambdaQueryWrapper<NinCourse>().eq(NinCourse::getMust, CourseTypeEnum.REQUIRED_COURSE.getCode())).stream().collect(Collectors.toMap(NinCourse::getId, Function.identity()));
        Map<Long, String> collect1 = ninClassMapper.selectList(new LambdaQueryWrapper<NinClass>().select(NinClass::getId, NinClass::getClassName)).stream().collect(Collectors.toMap(NinClass::getId, NinClass::getClassName));
        HashMap<Long, List<Long>> longListHashMap = new HashMap<>();

        ArrayList<NinArrange> ninArrangeArrayList = new ArrayList<>();
        ArrayList<NinTeachClass> ninTeachClasses = new ArrayList<>();

        for (TaskRecord taskRecord : caculte) {
            if (null == taskRecord.getTeaTask().getTeachClassId()) {
                continue;
            }
            NinArrange arrange = new NinArrange(taskRecord);
            NinCourse course = collect.get(arrange.getCourseId());
            if (arrange.getMust() == CourseTypeEnum.REQUIRED_COURSE.getCode()) {
                longListHashMap.put(taskRecord.getTeaTask().getTeachClassId(), taskRecord.getTeaTask().getClassIdList());
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
            ninArrangeArrayList.add(arrange);
        }

        for (Map.Entry<Long, List<Long>> map : longListHashMap.entrySet()) {
            List<Long> value = map.getValue();
            for (Long classId : value) {
                NinTeachClass ninTeachClass = new NinTeachClass();
                ninTeachClass.setClassId(classId);
                ninTeachClass.setTeachClassId(map.getKey());
                ninTeachClass.setClassName(collect1.get(classId));
                ninTeachClasses.add(ninTeachClass);
            }
        }

        ninArrangeService.saveBatch(ninArrangeArrayList);
        ninTeachClassService.saveBatch(ninTeachClasses);
        return ResultModel.ok();
    }

    /**
     * 清空记录
     */
    @GetMapping("nin-arrange/empty")
    public ResultModel empty() throws IOException, EncodeException {
        ninArrangeService.empty();
        return ResultModel.ok();
    }

    /**
     * 条件分页查询
     */
    @PostMapping("/nin-arrange/getPageSelectList")
    public ResultModel getPageSelectList(ArrangeBo bo, Integer page, Integer size) {
        return ResultModel.ok(ninArrangeService.getPageSelectList(bo, page, size));
    }

    /**
     * 根据id删除排课记录
     */
    @PostMapping("nin-arrange/delArrange")
    public ResultModel delArrange(Long id) {
        int i = ninArrangeService.delArrange(id);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /*--课程表--*/

    //跳转各管理的页面的课程表
    @GetMapping("/nin-arrange/courseForm")
    public ModelAndView gotoFormView() {
        return new ModelAndView("view/courseFormView");
    }

    /**
     * 获取课程表的信息
     * @return 12（星期一第二节课） -> 课程信息（String）
     */
    @PostMapping("/nin-arrange/getInfo")
    public ResultModel getInfo(Long classId, Long teacherId, Long studentId, Integer count) {
        return ResultModel.ok(ninArrangeService.getInfo(classId, teacherId, studentId, count));
    }

    /**
     * 导出课程表
     * @param type 用户类型
     * @param id id
     */
    @GetMapping("/exportCourseForm")
    public ResultModel exportCourseForm(String type, Long id, Integer count,  HttpServletRequest request, HttpServletResponse response) throws ParseException {
        ninArrangeService.exportCourseForm(type, id, count, request, response);
        return ResultModel.ok();
    }

    /*--教室申请--*/

    //跳转教室申请页面
    @GetMapping("/applyHouse")
    public ModelAndView gotoApplyHouseView() {
        ModelAndView modelAndView = new ModelAndView("view/applyHouseView");
        UserInfo userInfo = UserUtil.getUserInfo();
        if (userInfo.getUserType().equals(UserTypeEnum.TEACHER.getName())) {
            modelAndView.addObject("teacherId", userInfo.getUserId().toString());
        }
        return modelAndView;
    }

    /**
     * 返回可申请的时间
     * @param bo
     * @return
     */
    @PostMapping("nin-arrange/getHouseApplyTime")
    public ResultModel getHouseApplyTime(HouseApplyBo bo) {
        return ResultModel.ok(ninArrangeService.getHouseApplyTime(bo));
    }

    /**
     * 提交申请
     */
    @PostMapping("/nin-arrange/submitApply")
    public ResultModel submitApply(HouseApplyBo bo) {
        return null;
    }

    /**
     * 添加排课记录（教室申请）
     */
    @PostMapping("/nin-arrange/addArrange")
    public ResultModel addArrange(HouseApplyBo bo) {
        int i = ninArrangeService.addArrange(bo);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "操作失败");
    }



    //跳转班级课程页面
    @GetMapping("nin-class-course")
    public ModelAndView gotoClassCourse() {
        return new ModelAndView("view/selectCourseView");
    }
}
