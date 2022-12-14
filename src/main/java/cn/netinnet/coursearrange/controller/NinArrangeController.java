package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.ArrangeBo;
import cn.netinnet.coursearrange.bo.HouseBo;
import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.NinArrange;
import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.entity.UserInfo;
import cn.netinnet.coursearrange.enums.ResultEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.global.ResultEntry;
import cn.netinnet.coursearrange.mapper.NinStudentMapper;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.util.UserUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

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
    private NinStudentMapper ninStudentMapper;

    //跳转排课页面
    @GetMapping("/nin-arrange")
    public ModelAndView gotoView() {
        return new ModelAndView("view/arrangeView");
    }

    //跳转各管理的页面的课程表
    @GetMapping("/nin-arrange/courseForm")
    public ModelAndView gotoFormView(Long classId, Long studentId, Long teacherId, String type) {
        ModelAndView modelAndView = new ModelAndView("view/courseFormView");
        modelAndView.addObject("classId", String.valueOf(classId));
        modelAndView.addObject("studentId", String.valueOf(studentId));
        modelAndView.addObject("teacherId", String.valueOf(teacherId));
        modelAndView.addObject("type", type);
        return modelAndView;
    }

    //学生教师课程表
    @GetMapping("/stu-course-form")
    public ModelAndView stuCourseForm() {
        UserInfo userInfo = UserUtil.getUserInfo();
        if (userInfo.getUserType().equals(ApplicationConstant.TYPE_STUDENT)) {
            ModelAndView modelAndView = new ModelAndView("view/courseFormView_1");
            modelAndView.addObject("studentId", String.valueOf(userInfo.getUserId()));
            return modelAndView;
        }
        throw new ServiceException(ResultEnum.POET_ERROR);
    }
    @GetMapping("/class-course-form")
    public ModelAndView classCourseForm() {
        UserInfo userInfo = UserUtil.getUserInfo();
        if (userInfo.getUserType().equals(ApplicationConstant.TYPE_STUDENT)) {
            NinStudent ninStudent = ninStudentMapper.selectById(userInfo.getUserId());
            ModelAndView modelAndView = new ModelAndView("view/CourseFormView_1");
            modelAndView.addObject("classId", String.valueOf(ninStudent.getClassId()));
            return modelAndView;
        }
        throw new ServiceException(ResultEnum.POET_ERROR);
    }
    @GetMapping("/tea-course-form")
    public ModelAndView teaCourseForm() {
        UserInfo userInfo = UserUtil.getUserInfo();
        if (userInfo.getUserType().equals(ApplicationConstant.TYPE_TEACHER)) {
            ModelAndView modelAndView = new ModelAndView("view/courseFormView_1");
            modelAndView.addObject("teacherId", String.valueOf(userInfo.getUserId()));
            return modelAndView;
        }
        throw new ServiceException(ResultEnum.POET_ERROR);
    }

    //跳转教室申请页面
    @GetMapping("/applyHouse")
    public ModelAndView gotoApplyHouseView() {
        ModelAndView modelAndView = new ModelAndView("view/applyHouseView");
        UserInfo userInfo = UserUtil.getUserInfo();
        if (userInfo.getUserType().equals(ApplicationConstant.TYPE_TEACHER)) {
            modelAndView.addObject("teacherId", userInfo.getUserId().toString());
        }
        return modelAndView;
    }

    /**
     * 自动排课
     */
    @GetMapping("/nin-arrange/arrange")
    public ResultEntry arrange() {
        ninArrangeService.arrange();
        return ResultEntry.ok();
    }

    /**
     * 清空记录
     */
    @GetMapping("nin-arrange/empty")
    public ResultEntry empty() {
        ninArrangeService.empty();
        return ResultEntry.ok();
    }

    /**
     * 获取课程表的信息
     * @return 12（星期一第二节课） -> 课程信息（String）
     */
    @PostMapping("/nin-arrange/getInfo")
    public ResultEntry getInfo(Long classId, Long teacherId, Long studentId, Integer count) {
        Map<String, String> info = ninArrangeService.getInfo(classId, teacherId, studentId, count);
        return ResultEntry.ok(info);
    }

    /**
     * 补课-获取空闲资源（教室申请）
     *
     * @param teacherId 教师id
     * @param classIds  班级id列表
     * @param houseId   教室id
     * @param houseType 教室类型
     * @param seatMin   座位
     * @param seatMax   座位
     * @param weekly    周次
     * @return
     */
    @PostMapping("/nin-arrange/getLeisure")
    public ResultEntry getLeisure(Long teacherId, String classIds, Long houseId, Integer houseType, Integer seatMin, Integer seatMax, Integer weekly) {
        Map<String, List<HouseBo>> leisure = ninArrangeService.getLeisure(teacherId, classIds, houseId, houseType, seatMin, seatMax, weekly);
        return ResultEntry.ok(leisure);
    }

    /**
     * 添加排课记录（教室申请）
     * @param weekly 第几周
     */
    @PostMapping("/nin-arrange/addArrange")
    public ResultEntry addArrange(Integer weekly, Integer week, Integer pitchNum, Long houseId, Long teacherId, Long courseId, String classIdList) {
        int i = ninArrangeService.addArrange(weekly, week, pitchNum, houseId, teacherId, courseId, classIdList);
        if (i > 0) {
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 条件分页查询
     */
    @PostMapping("/nin-arrange/getPageSelectList")
    public ResultEntry getPageSelectList(ArrangeBo bo, Integer page, Integer size) {
        return ResultEntry.ok(ninArrangeService.getPageSelectList(bo, page, size));
    }

    /**
     * 根据id删除排课记录
     */
    @PostMapping("nin-arrange/delArrange")
    public ResultEntry delArrange(Long id) {
        int i = ninArrangeService.delArrange(id);
        if (i > 0) {
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 编辑排课记录（未使用）
     */
    @PostMapping("nin-arrange/alterArrange")
    public ResultEntry alterArrange(NinArrange arrange) {
        int i = ninArrangeService.alterArrange(arrange);
        if (i > 0) {
            return ResultEntry.ok();
        }
        return ResultEntry.error(412, "编辑失败");
    }


    /**
     * 根据课程id获取可选教师教师及时间（未使用）
     */
    @PostMapping("nin-arrange/getTeacherHouseORTime")
    public ResultEntry getTeacherHouseORTime(Long courseId, Long teacherId, Long houseId) {
        List arrangeBo = ninArrangeService.getTeacherHouseORTime(courseId, teacherId, houseId);
        return ResultEntry.ok(arrangeBo);
    }

    /**
     * 导出课程表
     * @param type 用户类型
     * @param id id
     */
    @GetMapping("/exportCourseForm")
    public ResultEntry exportCourseForm(String type, Long id, Integer count,  HttpServletRequest request, HttpServletResponse response) throws ParseException {
        ninArrangeService.exportCourseForm(type, id, count, request, response);
        return ResultEntry.ok();
    }

    //跳转班级课程页面
    @GetMapping("nin-class-course")
    public ModelAndView gotoView(Long classId) {
        ModelAndView modelAndView = new ModelAndView("view/classCourseView");
        modelAndView.addObject("classId", String.valueOf(classId));
        return modelAndView;
    }
}
