package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.entity.NinTeacherCourse;
import cn.netinnet.coursearrange.entity.UserInfo;
import cn.netinnet.coursearrange.enums.ResultEnum;
import cn.netinnet.coursearrange.global.ResultEntry;
import cn.netinnet.coursearrange.service.INinTeacherCourseService;
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
@RequestMapping("/nin-teacher-course")
@RequiresRoles(value = {"admin", "teacher"}, logical = Logical.OR)
public class NinTeacherCourseController {
    @Autowired
    private INinTeacherCourseService ninTeacherCourseService;

    //跳转教师-课程页面
    @GetMapping("")
    public ModelAndView gotoTeacherCourse(Long teacherId){
        ModelAndView modelAndView = new ModelAndView();
        if (teacherId == null) {
            UserInfo userInfo = UserUtil.getUserInfo();
            if (userInfo.getUserType().equals("teacher")) {
                teacherId = userInfo.getUserId();
                modelAndView = new ModelAndView("viewTea/teacherCourseView");
            }
        } else {
            modelAndView = new ModelAndView("view/teacherCourseView");
        }
        modelAndView.addObject("teacherId", String.valueOf(teacherId));
        return modelAndView;
    }

    /**
     * 查询
     * @param teacherId
     * @return
     */
    @PostMapping("/getSelectList")
    public ResultEntry getSelectList(Long teacherId){
        List<ContactCourseBo> list = ninTeacherCourseService.getSelectList(teacherId);
        return ResultEntry.ok(list);
    }

    /**
     * 添加教师-课程记录
     * @param ninTeacherCourse
     * @return
     */
    @PostMapping("/addTeacherCourse")
    public ResultEntry addTeacherCourse(NinTeacherCourse ninTeacherCourse){
        int i = ninTeacherCourseService.addSingle(ninTeacherCourse);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 删除教师-课程记录
     * @param id
     * @return
     */
    @PostMapping("/delTeacherCourse")
    public ResultEntry delTeacherCourse(Long id){
        int i = ninTeacherCourseService.delById(id);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

}
