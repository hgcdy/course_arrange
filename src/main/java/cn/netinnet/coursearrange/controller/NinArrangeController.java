package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.ArrangeBo;
import cn.netinnet.coursearrange.bo.HouseBo;
import cn.netinnet.coursearrange.constant.ApplicationConstant;
import cn.netinnet.coursearrange.entity.NinArrange;
import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.entity.UserInfo;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinStudentMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.util.UserUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public ModelAndView gotoFormView() {
        return new ModelAndView("view/courseFormView");
    }

    @GetMapping("/courseForm/{type}")
    public ModelAndView getCourseForm(@PathVariable String type) {
        UserInfo userInfo = UserUtil.getUserInfo();
        String userType = userInfo.getUserType();
        ModelAndView modelAndView = new ModelAndView("view/courseFormView_1");
        if (userType.equals(ApplicationConstant.TYPE_STUDENT)) {
            if ("stu".equals(type)) {
                modelAndView.addObject("studentId", String.valueOf(userInfo.getUserId()));
                return modelAndView;
            } else if ("class".equals(type)) {
                NinStudent ninStudent = ninStudentMapper.selectById(userInfo.getUserId());
                modelAndView.addObject("classId", String.valueOf(ninStudent.getClassId()));
                return modelAndView;
            }
        } else if (userType.equals(ApplicationConstant.TYPE_TEACHER)) {
            modelAndView.addObject("teacherId", String.valueOf(userInfo.getUserId()));
            return modelAndView;
        }
        throw new ServiceException(412, "接口错误");
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
    public ResultModel arrange() {
        ninArrangeService.arrange();
        return ResultModel.ok();
    }

    /**
     * 清空记录
     */
    @GetMapping("nin-arrange/empty")
    public ResultModel empty() {
        ninArrangeService.empty();
        return ResultModel.ok();
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
    public ResultModel getLeisure(Long teacherId, String classIds, Long houseId, Integer houseType, Integer seatMin, Integer seatMax, Integer weekly) {
        Map<String, List<HouseBo>> leisure = ninArrangeService.getLeisure(teacherId, classIds, houseId, houseType, seatMin, seatMax, weekly);
        return ResultModel.ok(leisure);
    }

    /**
     * 添加排课记录（教室申请）
     * @param weekly 第几周
     */
    @PostMapping("/nin-arrange/addArrange")
    public ResultModel addArrange(Integer weekly, Integer week, Integer pitchNum, Long houseId, Long teacherId, Long courseId, String classIdList) {
        int i = ninArrangeService.addArrange(weekly, week, pitchNum, houseId, teacherId, courseId, classIdList);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "操作失败");
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

    /**
     * 编辑排课记录（未使用）
     */
    @PostMapping("nin-arrange/alterArrange")
    public ResultModel alterArrange(NinArrange arrange) {
        int i = ninArrangeService.alterArrange(arrange);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "编辑失败");
    }


    /**
     * 根据课程id获取可选教师教师及时间（未使用）
     */
    @PostMapping("nin-arrange/getTeacherHouseORTime")
    public ResultModel getTeacherHouseORTime(Long courseId, Long teacherId, Long houseId) {
        List arrangeBo = ninArrangeService.getTeacherHouseORTime(courseId, teacherId, houseId);
        return ResultModel.ok(arrangeBo);
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


    //跳转班级课程页面
    @GetMapping("nin-class-course")
    public ModelAndView gotoClassCourse() {
        return new ModelAndView("view/classCourseView");
    }
}
