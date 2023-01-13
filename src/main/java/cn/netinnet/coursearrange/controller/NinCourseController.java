package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinCourse;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinCourseService;
import com.sun.istack.internal.NotNull;
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
@RequestMapping("/nin-course")
@RequiresRoles(value = {"admin", "teacher", "student"}, logical = Logical.OR)
public class NinCourseController {

    @Autowired
    private INinCourseService ninCourseService;

    //跳转课程页面
    @GetMapping("")
    public ModelAndView gotoView(){
        return new ModelAndView("view/CourseView");
    }

    /**
     * 分页选择查询
     * @param page
     * @param size
     * @param ninCourse
     * @return
     */
    @PostMapping("/getPageSelectList")
    public ResultModel getPageSelectList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         NinCourse ninCourse){
        Map<String, Object> map = ninCourseService.getPageSelectList(page, size, ninCourse);
        return ResultModel.ok(map);
    }

    //根据id获取课程信息
    @PostMapping("/getCourseById")
    public ResultModel getCourseById(Long id){
        return ResultModel.ok(ninCourseService.getCourseById(id));
    }

    /**
     * 课程新增
     * @param ninCourse
     * @return
     */
    @PostMapping("/addCourse")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultModel addCourse(NinCourse ninCourse){
        int i = ninCourseService.addSingle(ninCourse);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "新增失败");
    }

    /**
     * 课程删除
     * @param id
     * @return
     */
    @PostMapping("/delCourse")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultModel delCourse(@NotNull Long id){
        int i = ninCourseService.delById(id);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 课程修改
     * @param ninCourse
     * @return
     */
    @PostMapping("/alterCourse")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultModel alterCourse(NinCourse ninCourse){
        int i = ninCourseService.alterSingle(ninCourse);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "修改失败");
    }

    /**
     * 获取可选的课程列表
     * 0-选修，1-必修，null或其他全选
     * 如果不是admin，根据setting限制课程
     * @return
     */
    @PostMapping("/getSelectCourseList")
    public ResultModel getSelectCourseList(Integer sign){
        List<NinCourse> courseList = ninCourseService.getSelectCourseList(sign);
        return ResultModel.ok(courseList);
    }

    /**
     * 教室申请-获取可选课程
     * @param teacherId
     * @param classIdList
     * @param houseId
     * @return
     */
    @PostMapping("/getSelectApplyList")
    public ResultModel getSelectApplyList(Long teacherId, Long houseId, String classIdList) {
        return ResultModel.ok(ninCourseService.getSelectApplyList(teacherId, houseId, classIdList));
    }

    /**
     * 获取全部的课程
     * @return
     */
    @GetMapping("/getCourseAll")
    public ResultModel getCourseAll() {
        return ResultModel.ok(ninCourseService.getCourseAll());
    }

}
