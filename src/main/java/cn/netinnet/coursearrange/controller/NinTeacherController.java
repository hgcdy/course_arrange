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
@RequiresRoles(value = {"admin", "teacher"}, logical = Logical.OR)
public class NinTeacherController {

    @Autowired
    private INinTeacherService ninTeacherService;

    //跳转教师页面
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
    @GetMapping("/getPageSelectList")
    public ResultModel getPageSelectList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         String teacherName){
        Map<String, Object> map = ninTeacherService.getPageSelectList(page, size, teacherName);
        return ResultModel.ok(map);
    }



    /**
     * 新增教师
     * @param ninTeacher
     * @return
     */
    @PostMapping("/addTeacher")
    public ResultModel addTeacher(NinTeacher ninTeacher){
        boolean b = ninTeacherService.addSingle(ninTeacher);
        if (b){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "新增失败");
    }

    /**
     * 删除教师
     * @param id
     * @return
     */
    @PostMapping("/delTeacher")
    public ResultModel delTeacher(Long id){
        boolean b = ninTeacherService.delById(id);
        if (b){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 修改教师信息
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

    /**
     * 教室申请时查询所有教师（教师用户返回自己）
     * @return
     */
    @GetMapping("getTeaAll")
    public ResultModel getTeaAll() {
        return ResultModel.ok(ninTeacherService.getTeaAll());
    }

    /**
     * 根据id获取单条记录
     * @param id
     * @return
     */
    @GetMapping("/getTeacherById")
    public ResultModel getTeacherById(Long id){
        return ResultModel.ok(ninTeacherService.getTeacherById(id));
    }



}
