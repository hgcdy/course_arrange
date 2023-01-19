package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.ClassBo;
import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.entity.NinCourse;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinClassService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
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
@RequestMapping("/nin-class")
@RequiresRoles(value = {"admin", "student", "teacher"}, logical = Logical.OR)
public class NinClassController {
    @Autowired
    private INinClassService ninClassService;

    /**
     * 跳转班级页面
     * @return
     */
    @GetMapping("")
    public ModelAndView gotoView() {
        return new ModelAndView("view/classView");
    }


    /**
     * 班级条件分页查询
     *
     * @param page
     * @param size
     * @param careerId  专业id
     * @param college   学院
     * @param className 模糊查询班级名称
     * @return
     */
    @PostMapping("/getPageSelectList")
    public ResultModel getPageSelectList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         Long careerId, String college, String className) {
        Map<String, Object> map = ninClassService.getPageSelectList(page, size, college, careerId, className);

        return ResultModel.ok(map);
    }


    /**
     * 获取班级列表
     * @param college
     * @param careerId
     * @return
     */
    @PostMapping("/getClassList")
    public ResultModel getClassList(String college, Long careerId) {
        return ResultModel.ok(ninClassService.getClassList(college, careerId));
    }

    /**
     * 班级列表，按学院专业分组
     *
     * @return
     */
    @GetMapping("collegeCareerClassList")
    public ResultModel collegeCareerClassList() {
        Map<String, Map<String, List<ClassBo>>> stringMapMap = ninClassService.collegeCareerClassList();
        return ResultModel.ok(stringMapMap);
    }

    /**
     * 新增班级
     *
     * @param ninClass
     * @return
     */
    @PostMapping("/addClass")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultModel addClass(NinClass ninClass) {
        return ninClassService.addSingle(ninClass) ? ResultModel.ok() : ResultModel.error(412, "新增失败");
    }

    /**
     * 删除班级
     *
     * @param id 班级id
     * @return
     */
    @PostMapping("/delClass")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultModel delClass(Long id) {
        int i = ninClassService.delById(id);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }


    /**
     * 修改班级
     *
     * @param ninClass
     * @return
     */
    @PostMapping("/alterClass")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultModel alterClass(NinClass ninClass) {
        int i = ninClassService.alterSingle(ninClass);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "修改失败");
    }


    /**
     * 班级已课程
     * @param id
     * @return
     */
    @PostMapping("/getCourse")
    public ResultModel getCourse(Long id) {
        return ResultModel.ok(ninClassService.getCourse(id));
    }
}
