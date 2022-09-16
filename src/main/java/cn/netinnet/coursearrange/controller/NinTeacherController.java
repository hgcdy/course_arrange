package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinTeacherService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

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
@RequestMapping("/nin-teacher")
@RequiresRoles(value = {"admin"}, logical = Logical.OR)
public class NinTeacherController {

    @Autowired
    private INinTeacherService ninTeacherService;

    @GetMapping("")
    public ModelAndView gotoView(){
        return new ModelAndView("view/teacherView");
    }

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param teacherName
     * @return
     */
    @PostMapping("/getPageSelectList")
    public ResultModel getPageSelectList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         String teacherName){
        Map<String, Object> map = ninTeacherService.getPageSelectList(page, size, teacherName);
        return ResultModel.ok(map);
    }

    /**
     * 新增
     * @param ninTeacher
     * @return
     */
    @PostMapping("/addTeacher")
    public ResultModel addTeacher(NinTeacher ninTeacher){
        int i = ninTeacherService.addSingle(ninTeacher);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "新增失败");
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @PostMapping("/delTeacher")
    public ResultModel delTeacher(Long id){
        int i = ninTeacherService.delById(id);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 修改
     * @param ninTeacher
     * @return
     */
    @PostMapping("/alterTeacher")
    public ResultModel alterTeacher(NinTeacher ninTeacher){
        int i = ninTeacherService.alterSingle(ninTeacher);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "修改失败");
    }

    @PostMapping("/getTeacherById")
    public ResultModel getTeacherById(Long id){
        return ResultModel.ok(ninTeacherService.getTeacherById(id));
    }
}
