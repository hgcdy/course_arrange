package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.entity.NinStudentCourse;
import cn.netinnet.coursearrange.mapper.NinStudentCourseMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
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
@RequestMapping("/nin-student-course")
@RequiresRoles(value = {"admin"}, logical = Logical.OR)
public class NinStudentCourseController {
    @Autowired
    private INinStudentCourseService ninStudentCourseService;

    @GetMapping("")
    public ModelAndView gotoView(Long studentId){
        ModelAndView modelAndView = new ModelAndView("view/studentCourseView");
        modelAndView.addObject("studentId", String.valueOf(studentId));
        return modelAndView;
    }

    @PostMapping("/getSelectList")
    public ResultModel getSelectList(Long studentId){
        List<List<Map<String, Object>>> selectList = ninStudentCourseService.getSelectList(studentId);
        return ResultModel.ok(selectList);
    }

    @PostMapping("/addStudentCourse")
    public ResultModel addStudent(NinStudentCourse ninStudentCourse){
        int i = ninStudentCourseService.addSingle(ninStudentCourse);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "新增失败");
    }

    @PostMapping("/delStudentCourse")
    public ResultModel delStudent(Long id){
        int i = ninStudentCourseService.delSingle(id);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }














}
