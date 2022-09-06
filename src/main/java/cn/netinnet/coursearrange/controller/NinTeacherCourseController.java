package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinHouse;
import cn.netinnet.coursearrange.entity.NinTeacherCourse;
import cn.netinnet.coursearrange.mapper.NinTeacherCourseMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinTeacherCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@RestController
@RequestMapping("/nin-teacher-course")
public class NinTeacherCourseController {
    @Autowired
    private INinTeacherCourseService ninTeacherCourseService;

    @GetMapping("")
    public ModelAndView gotoTeacherCourse(Long teacherId){
        ModelAndView modelAndView = new ModelAndView("view/teacherCourseView");
        modelAndView.addObject("teacherId", String.valueOf(teacherId));
        return modelAndView;
    }

    @PostMapping("/getSelectList")
    public ResultModel getSelectList(Long teacherId){
        List<Map<String, Object>> list = ninTeacherCourseService.getPageSelectList(teacherId);
        return ResultModel.ok(list);
    }

    @PostMapping("/addTeacherCourse")
    public ResultModel addTeacherCourse(NinTeacherCourse ninTeacherCourse){
        int i = ninTeacherCourseService.addSingle(ninTeacherCourse);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "新增失败");
    }

    @PostMapping("/delTeacherCourse")
    public ResultModel delTeacherCourse(Long id){
        int i = ninTeacherCourseService.delById(id);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

}
