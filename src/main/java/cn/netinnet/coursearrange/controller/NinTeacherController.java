package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.enums.ResultEnum;
import cn.netinnet.coursearrange.global.ResultEntry;
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
    @PostMapping("/getPageSelectList")
    public ResultEntry getPageSelectList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         String teacherName){
        Map<String, Object> map = ninTeacherService.getPageSelectList(page, size, teacherName);
        return ResultEntry.ok(map);
    }



    /**
     * 新增教师
     * @param ninTeacher
     * @return
     */
    @PostMapping("/addTeacher")
    public ResultEntry addTeacher(NinTeacher ninTeacher){
        int i = ninTeacherService.addSingle(ninTeacher);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 删除教师
     * @param id
     * @return
     */
    @PostMapping("/delTeacher")
    public ResultEntry delTeacher(Long id){
        int i = ninTeacherService.delById(id);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 修改教师信息
     * @param ninTeacher
     * @return
     */
    @PostMapping("/alterTeacher")
    public ResultEntry alterTeacher(NinTeacher ninTeacher){
        int i = ninTeacherService.alterSingle(ninTeacher);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 教室申请时查询所有教师（教师用户返回自己）
     * @return
     */
    @PostMapping("getTeaAll")
    public ResultEntry getTeaAll() {
        return ResultEntry.ok(ninTeacherService.getTeaAll());
    }

    /**
     * 根据id获取单条记录
     * @param id
     * @return
     */
    @GetMapping("/getTeacherById")
    public ResultEntry getTeacherById(Long id){
        return ResultEntry.ok(ninTeacherService.getTeacherById(id));
    }

    /**
     * 教师修改密码
     * @param code
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequiresRoles(value = {"teacher"}, logical = Logical.OR)
    @PostMapping("/alterTeaPassword")
    public ResultEntry alterPassword(String code, String oldPassword, String newPassword){
        return ninTeacherService.alterPassword(code, oldPassword, newPassword);
    }


}
