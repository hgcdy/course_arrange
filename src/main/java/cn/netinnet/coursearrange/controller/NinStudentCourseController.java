package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinStudentCourse;
import cn.netinnet.coursearrange.entity.UserInfo;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import cn.netinnet.coursearrange.util.UserUtil;
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
@RequiresRoles(value = {"admin", "student"}, logical = Logical.OR)
public class NinStudentCourseController {
    @Autowired
    private INinStudentCourseService ninStudentCourseService;

    /**
     * 跳转学生-课程页面
     * @param studentId
     * @return
     */
    @GetMapping("")
    public ModelAndView gotoView(Long studentId){
        ModelAndView modelAndView = new ModelAndView();
        if (studentId == null) {
            UserInfo userInfo = UserUtil.getUserInfo();
            if (userInfo.getUserType().equals("student")) {
                studentId = userInfo.getUserId();
                modelAndView = new ModelAndView("viewStu/studentCourseView");
            }
        } else {
            modelAndView = new ModelAndView("view/studentCourseView");
        }
        modelAndView.addObject("studentId", String.valueOf(studentId));
        return modelAndView;
    }

    //查询
    @PostMapping("/getSelectList")
    public ResultModel getSelectList(Long studentId){
        List<List<Map<String, Object>>> selectList = ninStudentCourseService.getSelectList(studentId);
        return ResultModel.ok(selectList);
    }

    //添加记录
    @PostMapping("/addStudentCourse")
    public ResultModel addStudent(NinStudentCourse ninStudentCourse){
        int i = ninStudentCourseService.addSingle(ninStudentCourse);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "新增失败");
    }

    //删除记录
    @PostMapping("/delStudentCourse")
    public ResultModel delStudent(Long id){
        int i = ninStudentCourseService.delSingle(id);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }














}
