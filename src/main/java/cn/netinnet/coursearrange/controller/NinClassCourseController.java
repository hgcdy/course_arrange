package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinClassCourse;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinClassCourseService;
import com.alibaba.fastjson.JSON;
import com.sun.istack.internal.NotNull;
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
@RequestMapping("/nin-class-course")
@RequiresRoles(value = {"admin"}, logical = Logical.OR)
public class NinClassCourseController {

    @Autowired
    private INinClassCourseService ninClassCourseService;

    @GetMapping("")
    public ModelAndView gotoView(Long classId) {
        ModelAndView modelAndView = new ModelAndView("view/classCourseView");
        modelAndView.addObject("classId", String.valueOf(classId));
        return modelAndView;
    }

    /**
     * 该班级下的班级-课程表
     *
     * @param classId
     * @return
     */
    @PostMapping("/getSelectList")
    public ResultModel getSelectList(Long classId) {
        List<Map<String, Object>> list = ninClassCourseService.getSelectList(classId);
        return ResultModel.ok(list);
    }

    /**
     * 添加
     *
     * @param ninClassCourse
     * @return
     */
    @PostMapping("/addClassCourse")
    public ResultModel addClassCourse(NinClassCourse ninClassCourse) {
        int i = ninClassCourseService.addSingle(ninClassCourse);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "新增失败");
    }

    /**
     * 根据专业批量添加
     * @param career
     * @param className
     * @param courseId
     * @return
     */
    @PostMapping("/addBatchClassCourse")
    public ResultModel addBatchClassCourse(String career, String className, @NotNull Long courseId) {
        ninClassCourseService.addBatch(career, className, courseId);
        return ResultModel.ok();
    }

    /**
     * 删除该班级下的某个课程
     *
     * @param id
     * @return
     */
    @PostMapping("/delClassCourse")
    public ResultModel delClassCourse(Long id) {
        int i = ninClassCourseService.delById(id);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

}
