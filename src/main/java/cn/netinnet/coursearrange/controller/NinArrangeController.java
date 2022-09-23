package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.NinArrangeBo;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinArrangeService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
@RequiresRoles(value = {"admin"}, logical = Logical.OR)
public class NinArrangeController {
    @Autowired
    private INinArrangeService ninArrangeService;

    //挑战排课页面
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

    @GetMapping("/applyHouse")
    public ModelAndView gotoApplyHouseView() {
        return new ModelAndView("view/applyHouseView");
    }

    //自动排课
    @GetMapping("/nin-arrange/arrange")
    public ResultModel arrange() {
        ninArrangeService.arrange();
        return ResultModel.ok();
    }

    //获取课程表的信息
    @PostMapping("/nin-arrange/getInfo")
    public ResultModel getInfo(Long classId, Long teacherId, Long studentId, Integer count) {
        Map<String, String> info = ninArrangeService.getInfo(classId, teacherId, studentId, count);
        return ResultModel.ok(info);
    }

    /**
     * 获取空闲资源（教室申请）
     * @param teacherId 教师id
     * @param classIds 班级id列表
     * @param houseId 教室id
     * @param houseType 教室类型
     * @param seatMin 座位
     * @param seatMax 座位
     * @param weekly 周次
     * @return
     */
    @PostMapping("/nin-arrange/getLeisure")
    public ResultModel getLeisure(Long teacherId, String classIds, Long houseId, Integer houseType, Integer seatMin, Integer seatMax, Integer weekly) {
        Map<String, List<Map<String, Object>>> leisure = ninArrangeService.getLeisure(teacherId, classIds, houseId, houseType, seatMin, seatMax, weekly);
        return ResultModel.ok(leisure);
    }


    @PostMapping("/nin-arrange/addArrange")
    public ResultModel addArrange(Integer weekly, Integer week, Integer pitchNum, Long houseId, Long teacherId, Long courseId, String classIdList) {
        ninArrangeService.addArrange(weekly, week, pitchNum, houseId, teacherId, courseId, classIdList);
        return null;
    }


    @PostMapping("/nin-arrange/getPageSelectList")
    public ResultModel getPageSelectList(NinArrangeBo bo, Integer page, Integer size) {
        return ResultModel.ok(ninArrangeService.getPageSelectList(bo, page, size));
    }




}
