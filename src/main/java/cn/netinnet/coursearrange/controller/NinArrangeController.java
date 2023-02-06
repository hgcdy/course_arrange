package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.ArrangeBo;
import cn.netinnet.coursearrange.bo.HouseApplyBo;
import cn.netinnet.coursearrange.domain.UserInfo;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.util.UserUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@RestController
@RequestMapping("")
@RequiresRoles(value = {"admin", "teacher", "student"}, logical = Logical.OR)
public class NinArrangeController {
    @Autowired
    private INinArrangeService ninArrangeService;


    /*--排课--*/
    //跳转排课页面
    @GetMapping("/nin-arrange")
    public ModelAndView gotoView() {
        return new ModelAndView("view/arrangeView");
    }

    /**
     * 自动排课
     */
    @GetMapping("/nin-arrange/arrange")
    public ResultModel arrange() {
        ninArrangeService.arrange();
        return ResultModel.ok();
    }

    /**
     * 清空记录
     */
    @GetMapping("nin-arrange/empty")
    public ResultModel empty(){
        ninArrangeService.empty();
        return ResultModel.ok();
    }

    /**
     * 条件分页查询
     */
    @PostMapping("/nin-arrange/getPageSelectList")
    public ResultModel getPageSelectList(ArrangeBo bo, Integer page, Integer size) {
        return ResultModel.ok(ninArrangeService.getPageSelectList(bo, page, size));
    }

    /**
     * 根据id删除排课记录
     */
    @PostMapping("nin-arrange/delArrange")
    public ResultModel delArrange(Long id) {
        int i = ninArrangeService.delArrange(id);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 根据排课记录返回可选教室
     */
    @GetMapping("nin-arrange/getHouseByArrangeId")
    public ResultModel getHouseByArrangeId(Long id) {
        return ResultModel.ok(ninArrangeService.getHouseByArrangeId(id));
    }

    @PostMapping("nin-arrange/alterArrange")
    public ResultModel alterArrange(Long id, Long houseId, Integer week, Integer pitchNum) {
        boolean b = ninArrangeService.alterArrange(id, houseId, week, pitchNum);
        if (b) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "编辑失败");
    }

    /*--课程表--*/

    //跳转各管理的页面的课程表
    @GetMapping("/nin-arrange/courseForm")
    public ModelAndView gotoFormView() {
        return new ModelAndView("view/courseFormView");
    }

    /**
     * 获取课程表的信息
     * @return 12（星期一第二节课） -> 课程信息（String）
     */
    @PostMapping("/nin-arrange/getInfo")
    public ResultModel getInfo(Long classId, Long teacherId, Long studentId, Integer count) {
        return ResultModel.ok(ninArrangeService.getInfo(classId, teacherId, studentId, count));
    }

    /**
     * 导出课程表
     * @param type 用户类型
     * @param id id
     */
    @GetMapping("/exportCourseForm")
    public ResultModel exportCourseForm(String type, Long id, Integer count,  HttpServletRequest request, HttpServletResponse response) throws ParseException {
        ninArrangeService.exportCourseForm(type, id, count, request, response);
        return ResultModel.ok();
    }

    /*--教室申请--*/

    //跳转教室申请页面
    @GetMapping("/applyHouse")
    public ModelAndView gotoApplyHouseView() {
        ModelAndView modelAndView = new ModelAndView("view/applyHouseView");
        UserInfo userInfo = UserUtil.getUserInfo();
        if (userInfo.getUserType().equals(UserTypeEnum.TEACHER.getName())) {
            modelAndView.addObject("teacherId", userInfo.getUserId().toString());
        }
        return modelAndView;
    }
    /**
     * 返回可申请的时间
     * @param bo
     * @return
     */
    @PostMapping("nin-arrange/getHouseApplyTime")
    public ResultModel getHouseApplyTime(HouseApplyBo bo) {
        return ResultModel.ok(ninArrangeService.getHouseApplyTime(bo));
    }
    /**
     * 提交申请
     */
    @PostMapping("/nin-arrange/submitApply")
    public ResultModel submitApply(HouseApplyBo bo) {
        return ninArrangeService.submitApply(bo);
    }


    //跳转班级课程页面
    @GetMapping("nin-class-course")
    public ModelAndView gotoClassCourse() {
        return new ModelAndView("view/selectCourseView");
    }
}
