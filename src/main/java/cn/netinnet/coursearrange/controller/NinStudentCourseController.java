package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

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
@RequestMapping("/nin-student-course")
@RequiresRoles(value = {"admin", "student"}, logical = Logical.OR)
public class NinStudentCourseController {
    @Autowired
    private INinStudentCourseService ninStudentCourseService;

    Lock lock = new ReentrantLock();

    /**
     * 跳转学生-课程页面
     */
    @GetMapping("")
    public ModelAndView gotoStudentCourse(){
        return new ModelAndView("view/selectCourseView");
    }

    @GetMapping("/getCourse")
    public ResultModel getSelectCourse(Long id) {
        return ResultModel.ok(ninStudentCourseService.getCourse(id));
    }

    /**
     * 添加记录
     * @return
     */
    @PostMapping("/addCourse")
    public ResultModel addStudent(Long id, Long courseId){
        try {
            lock.lock();
            boolean b = ninStudentCourseService.addSingle(id, courseId);
            if (b) {
                return ResultModel.ok();
            }
            return ResultModel.error(412, "新增失败");
        } finally {
            lock.unlock();
        }
    }

    /**
     * 删除记录
     * @param id
     * @return
     */
    @PostMapping("/delCourse")
    public ResultModel delStudent(Long id, Long courseId){
        try {
            lock.lock();
            boolean b = ninStudentCourseService.delSingle(id, courseId);
            if (b) {
                return ResultModel.ok();
            }
            return ResultModel.error(412, "删除失败");
        } finally {
            lock.unlock();
        }
    }


}
