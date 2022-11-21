package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.enums.ResultEnum;
import cn.netinnet.coursearrange.global.ResultEntry;
import cn.netinnet.coursearrange.service.INinStudentService;
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
@RequestMapping("/nin-student")
@RequiresRoles(value = {"admin"}, logical = Logical.OR)
public class NinStudentController {

    @Autowired
    private INinStudentService ninStudentService;

    //跳转学生页面
    @GetMapping("")
    public ModelAndView gotoStudent(){
        return new ModelAndView("view/studentView");
    }

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param studentName 名字或账号模糊查询
     * @return
     */
    @PostMapping("/getPageSelectList")
    public ResultEntry getPageSelectList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         String college, Long careerId, Long classId, String studentName){
        Map<String, Object> map = ninStudentService.getPageSelectList(page, size, college, careerId, classId, studentName);
        return ResultEntry.ok(map);
    }


    /**
     * 新增学生
     * @param ninStudent
     * @return
     */
    @PostMapping("/addStudent")
    public ResultEntry addStudent(NinStudent ninStudent){
        int i = ninStudentService.addSingle(ninStudent);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 删除学生
     * @param id
     * @return
     */
    @PostMapping("/delStudent")
    public ResultEntry delStudent(Long id){
        int i = ninStudentService.delById(id);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }



    /**
     * 修改学生信息
     * @param ninStudent
     * @return
     */
    @PostMapping("alterStudent")
    public ResultEntry alterStudent(NinStudent ninStudent){
        int i = ninStudentService.alterSingle(ninStudent);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 根据id获取单条记录
     * @param id
     * @return
     */
    @GetMapping("/getStudentById")
    public ResultEntry getStudentById(Long id){
        return ResultEntry.ok(ninStudentService.getStudentById(id));
    }

    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @GetMapping("/getStuUserInfo")
    public ResultEntry getStuUserInfo(Long id) {
        return null;
    }

    /**
     * 学生修改密码
     * @param code
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequiresRoles(value = {"student"}, logical = Logical.OR)
    @PostMapping("/alterStuPassword")
    public ResultEntry alterPassword(String code, String oldPassword, String newPassword){
        return ninStudentService.alterPassword(code, oldPassword, newPassword);
    }

}
