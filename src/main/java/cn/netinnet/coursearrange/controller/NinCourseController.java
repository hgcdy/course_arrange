package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinCourse;
import cn.netinnet.coursearrange.enums.ResultEnum;
import cn.netinnet.coursearrange.global.ResultEntry;
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
    public ResultEntry getPageSelectList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         NinCourse ninCourse){
        Map<String, Object> map = ninCourseService.getPageSelectList(page, size, ninCourse);
        return ResultEntry.ok(map);
    }

    //根据id获取课程信息
    @PostMapping("/getCourseById")
    public ResultEntry getCourseById(Long id){
        return ResultEntry.ok(ninCourseService.getCourseById(id));
    }

    /**
     * 课程新增
     * @param ninCourse
     * @return
     */
    @PostMapping("/addCourse")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultEntry addCourse(NinCourse ninCourse){
        int i = ninCourseService.addSingle(ninCourse);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 课程删除
     * @param id
     * @return
     */
    @PostMapping("/delCourse")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultEntry delCourse(@NotNull Long id){
        int i = ninCourseService.delById(id);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 课程修改
     * @param ninCourse
     * @return
     */
    @PostMapping("/alterCourse")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultEntry alterCourse(NinCourse ninCourse){
        int i = ninCourseService.alterSingle(ninCourse);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 获取可选的课程列表
     * 0-选修，1-必修，null或其他全选
     * 如果不是admin，根据setting限制课程
     * @return
     */
    @PostMapping("/getSelectCourseList")
    public ResultEntry getSelectCourseList(Integer sign){
        List<NinCourse> courseList = ninCourseService.getSelectCourseList(sign);
        return ResultEntry.ok(courseList);
    }

    /**
     * 教室申请-获取可选课程
     * @param teacherId
     * @param classIdList
     * @param houseId
     * @return
     */
    @PostMapping("/getSelectApplyList")
    public ResultEntry getSelectApplyList(Long teacherId, Long houseId, String classIdList) {
        return ResultEntry.ok(ninCourseService.getSelectApplyList(teacherId, houseId, classIdList));
    }

}
