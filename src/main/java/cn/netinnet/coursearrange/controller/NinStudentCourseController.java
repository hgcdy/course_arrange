package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.entity.NinStudentCourse;
import cn.netinnet.coursearrange.entity.UserInfo;
import cn.netinnet.coursearrange.enums.ResultEnum;
import cn.netinnet.coursearrange.global.ResultEntry;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import cn.netinnet.coursearrange.util.UserUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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

    /**
     * 查询
     * @param studentId
     * @return
     */
    @PostMapping("/getSelectList")
    public ResultEntry getSelectList(Long studentId){
        List<List<ContactCourseBo>> selectList = ninStudentCourseService.getSelectList(studentId);
        return ResultEntry.ok(selectList);
    }

    /**
     * 添加记录
     * @param ninStudentCourse
     * @return
     */
    @PostMapping("/addStudentCourse")
    public ResultEntry addStudent(NinStudentCourse ninStudentCourse){
        int i = ninStudentCourseService.addSingle(ninStudentCourse);
        if (i > 0) {
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 删除记录
     * @param id
     * @return
     */
    @PostMapping("/delStudentCourse")
    public ResultEntry delStudent(Long id){
        int i = ninStudentCourseService.delSingle(id);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }


}
