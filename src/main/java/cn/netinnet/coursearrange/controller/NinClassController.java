package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.model.ResultModel;
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
@RequiresRoles(value = {"admin"}, logical = Logical.OR)
public class NinClassController {
    @Autowired
    private INinClassService ninClassService;

    @GetMapping("")
    public ModelAndView gotoView() {
        return new ModelAndView("view/classView");
    }

    /**
     * 条件分页查询
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
     * 课程列表
     * @param classId
     * @return
     */
    @PostMapping("/getCourseList")
    public ResultModel getCourseList(Long classId){
        List<Map<String, Object>> list = ninClassService.getSelectList(classId);
        return ResultModel.ok(list);
    }


    @GetMapping("collegeCareerClassList")
    public ResultModel collegeCareerClassList(){
        Map<String, Map<String, List<Map<String, Object>>>> stringMapMap = ninClassService.collegeCareerClassList();
        return ResultModel.ok(stringMapMap);
    }




    /**
     * 新增
     *
     * @param ninClass
     * @return
     */
    @PostMapping("/addClass")
    public ResultModel addClass(NinClass ninClass) {
        int i = ninClassService.addSingle(ninClass);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "新增失败");
    }

    /**
     * 删除班级
     *
     * @param id 班级id
     * @return
     */
    @PostMapping("/delClass")
    public ResultModel delClass(Long id) {
        int i = ninClassService.delById(id);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 删除该班级的学生及记录
     *
     * @param id 班级id
     * @return
     */
    @PostMapping("/delClassStudent")
    public ResultModel delClassStudent(Long id) {
        int i = ninClassService.delBatchStudent(id);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 修改
     *
     * @param ninClass
     * @return
     */
    @PostMapping("/alterClass")
    public ResultModel alterClass(NinClass ninClass) {
        int i = ninClassService.alterSingle(ninClass);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "修改失败");
    }

}
