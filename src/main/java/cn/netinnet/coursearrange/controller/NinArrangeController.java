package cn.netinnet.coursearrange.controller;


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
@RequestMapping("/nin-arrange")
@RequiresRoles(value = {"admin"}, logical = Logical.OR)
public class NinArrangeController {
    @Autowired
    private INinArrangeService ninArrangeService;

    @GetMapping("")
    public ModelAndView gotoView() {
        return new ModelAndView("view/arrangeView");
    }


    @GetMapping("/courseForm")
    public ModelAndView gotoFormView(Long classId, Long studentId, Long teacherId, String type) {
        ModelAndView modelAndView = new ModelAndView("view/courseFormView");
        modelAndView.addObject("classId", String.valueOf(classId));
        modelAndView.addObject("studentId", String.valueOf(studentId));
        modelAndView.addObject("teacherId", String.valueOf(teacherId));
        modelAndView.addObject("type", type);
        return modelAndView;
    }

    @GetMapping("/arrange")
    public ResultModel arrange() {
        ninArrangeService.arrange();
        return ResultModel.ok();
    }

    @PostMapping("/getInfo")
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
     * @param seat 座位
     * @param weekly 周次
     * @param week 星期
     * @param pitchNum 节数
     * @return
     */
    @PostMapping("/getLeisure")
    public ResultModel getLeisure(Long teacherId, String classIds, Long houseId, Integer houseType, String seat, Integer weekly, Integer week, Integer pitchNum) {
        return null;
    }



}
