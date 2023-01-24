package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.entity.NinTeacherCourse;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinTeacherCourseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    Lock lock = new ReentrantLock();

    //跳转教师-课程页面
    @GetMapping("")
    public ModelAndView gotoTeacherCourse(){
        return new ModelAndView("view/selectCourseView");
    }

    @PostMapping("/getCourse")
    public ResultModel getSelectCourse(Long id) {
        return ResultModel.ok(ninTeacherCourseService.getCourse(id));
    }

    /**
     * 添加教师-课程记录
     * @return
     */
    @PostMapping("/addCourse")
    public ResultModel addTeacherCourse(Long id, Long courseId){
        try {
            lock.lock();
            int i = ninTeacherCourseService.addSingle(id, courseId);
            return i > 0 ? ResultModel.ok() : ResultModel.error(412, "新增失败");
        } finally {
            lock.unlock();
        }
    }

    /**
     * 删除教师-课程记录
     * @param id
     * @return
     */
    @PostMapping("/delCourse")
    public ResultModel delTeacherCourse(Long id, Long courseId){
        try {
            lock.lock();
            int i = ninTeacherCourseService.delSingle(id, courseId);
            return i > 0 ? ResultModel.ok() : ResultModel.error(412, "删除失败");
        } finally {
            lock.unlock();
        }
    }

}
