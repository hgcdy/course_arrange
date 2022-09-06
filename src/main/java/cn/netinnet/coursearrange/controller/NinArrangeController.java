package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinArrangeService;
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
public class NinArrangeController {
    @Autowired
    private INinArrangeService ninArrangeService;

    @GetMapping("")
    public ModelAndView gotoView() {
        return new ModelAndView("view/arrangeView");
    }

    @GetMapping("/courseForm")
    public ModelAndView gotoFormView() {
        return new ModelAndView("view/courseFormView");
    }

    @GetMapping("/arrange")
    public ResultModel arrange() {
        ninArrangeService.arrange();
        return ResultModel.ok();
    }

    @PostMapping("/getInfo")
    public ResultModel getInfo(Long classId, Long teacherId, Long studentId, Integer weekly) {
        Map<String, String> info = ninArrangeService.getInfo(classId, teacherId, studentId, weekly);
        return ResultModel.ok(info);
    }


}
