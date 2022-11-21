package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.ClassBo;
import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.entity.NinCourse;
import cn.netinnet.coursearrange.enums.ResultEnum;
import cn.netinnet.coursearrange.global.ResultEntry;
import cn.netinnet.coursearrange.service.INinClassService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public ResultEntry getPageSelectList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         Long careerId, String college, String className) {
        Map<String, Object> map = ninClassService.getPageSelectList(page, size, college, careerId, className);

        return ResultEntry.ok(map);
    }

    /**
     * 班级-课程记录查询
     * 根据班级查询课程
     * @param classId
     * @return
     */
    @PostMapping("/getCourseList")
    public ResultEntry getCourseList(Long classId) {
        List<NinCourse> list = ninClassService.getCourseList(classId);
        return ResultEntry.ok(list);
    }

    /**
     * 获取班级列表
     * @param college
     * @param careerId
     * @return
     */
    @PostMapping("/getClassList")
    public ResultEntry getClassList(String college, Long careerId) {
        return ResultEntry.ok(ninClassService.getClassList(college, careerId));
    }


    /**
     * 班级列表，按学院专业分组
     *
     * @return
     */
    @GetMapping("collegeCareerClassList")
    public ResultEntry collegeCareerClassList() {
        Map<String, Map<String, List<ClassBo>>> stringMapMap = ninClassService.collegeCareerClassList();
        return ResultEntry.ok(stringMapMap);
    }

    /**
     * 新增班级
     *
     * @param ninClass
     * @return
     */
    @PostMapping("/addClass")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultEntry addClass(NinClass ninClass) {
        int i = ninClassService.addSingle(ninClass);
        if (i > 0) {
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 删除班级
     *
     * @param id 班级id
     * @return
     */
    @PostMapping("/delClass")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultEntry delClass(Long id) {
        int i = ninClassService.delById(id);
        if (i > 0) {
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 删除该班级的学生及记录
     *
     * @param id 班级id
     * @return
     */
    @PostMapping("/delClassStudent")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultEntry delClassStudent(Long id) {
        int i = ninClassService.delBatchStudent(id);
        if (i > 0) {
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 修改班级
     *
     * @param ninClass
     * @return
     */
    @PostMapping("/alterClass")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    public ResultEntry alterClass(NinClass ninClass) {
        int i = ninClassService.alterSingle(ninClass);
        if (i > 0) {
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }
}
