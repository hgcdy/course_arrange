package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.NinArrangeBo;
import cn.netinnet.coursearrange.entity.UserInfo;
import cn.netinnet.coursearrange.mapper.NinArrangeMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
@RequestMapping("")
@RequiresRoles(value = {"admin", "teacher", "student"}, logical = Logical.OR)
public class NinArrangeController {
    @Autowired
    private INinArrangeService ninArrangeService;


    //跳转排课页面
    @GetMapping("/nin-arrange")
    public ModelAndView gotoView() {
        return new ModelAndView("view/arrangeView");
    }

    //跳转各管理的页面的课程表
    @GetMapping("/nin-arrange/courseForm")
    public ModelAndView gotoFormView(Long classId, Long studentId, Long teacherId, String type) {
        ModelAndView modelAndView = new ModelAndView("view/courseFormView");
        modelAndView.addObject("classId", String.valueOf(classId));
        modelAndView.addObject("studentId", String.valueOf(studentId));
        modelAndView.addObject("teacherId", String.valueOf(teacherId));
        modelAndView.addObject("type", type);
        return modelAndView;
    }

    //教室申请页面
    @GetMapping("/applyHouse")
    public ModelAndView gotoApplyHouseView() {
        ModelAndView modelAndView = new ModelAndView("view/applyHouseView");
        UserInfo userInfo = UserUtil.getUserInfo();
        if (userInfo.getUserType().equals("teacher")) {
            modelAndView.addObject("teacherId", userInfo.getUserId().toString());
        }
        return modelAndView;
    }

    //自动排课
    @GetMapping("/nin-arrange/arrange")
    public ResultModel arrange() {
        ninArrangeService.arrange();
        return ResultModel.ok();
    }

    /**
     * 清空记录
     * @return
     */
    @GetMapping("nin-arrange/empty")
    public ResultModel empty() {
        ninArrangeService.empty();
        return ResultModel.ok();
    }

    //获取课程表的信息
    @PostMapping("/nin-arrange/getInfo")
    public ResultModel getInfo(Long classId, Long teacherId, Long studentId, Integer count) {
        Map<String, String> info = ninArrangeService.getInfo(classId, teacherId, studentId, count);
        return ResultModel.ok(info);
    }

    /**
     * 获取空闲资源（教室申请）
     * @param teacherId 教师id
     * @param classIds 班级id列表
     * @param houseId 教室id
     * @param houseType 教室类型
     * @param seatMin 座位
     * @param seatMax 座位
     * @param weekly 周次
     * @return
     */
    @PostMapping("/nin-arrange/getLeisure")
    public ResultModel getLeisure(Long teacherId, String classIds, Long houseId, Integer houseType, Integer seatMin, Integer seatMax, Integer weekly) {
        Map<String, List<Map<String, Object>>> leisure = ninArrangeService.getLeisure(teacherId, classIds, houseId, houseType, seatMin, seatMax, weekly);
        return ResultModel.ok(leisure);
    }


    //添加
    @PostMapping("/nin-arrange/addArrange")
    public ResultModel addArrange(Integer weekly, Integer week, Integer pitchNum, Long houseId, Long teacherId, Long courseId, String classIdList) {
        ninArrangeService.addArrange(weekly, week, pitchNum, houseId, teacherId, courseId, classIdList);
        return null;
    }


    //查询
    @PostMapping("/nin-arrange/getPageSelectList")
    public ResultModel getPageSelectList(NinArrangeBo bo, Integer page, Integer size) {
        return ResultModel.ok(ninArrangeService.getPageSelectList(bo, page, size));
    }

    //删除
    @PostMapping("nin-arrange/delArrange")
    public ResultModel delArrange(Long id) {
        int i = ninArrangeService.delArrange(id);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }


    //编辑
    @PostMapping("nin-arrange/alterArrange")
    public ResultModel alterArrange(NinArrangeBo bo) {
        ninArrangeService.alterArrange(bo);
        return null;
    }

    /**
     * 选修课添加教室教师时可选的资源
     * @return
     */
    @PostMapping("/getAvailable")
    ResultModel getAvailable(Long id, Long teacherId, Long houseId, Integer week, Integer pitchNum){
        Map<String, List> available = ninArrangeService.getAvailable(id, teacherId, houseId, week, pitchNum);
        return ResultModel.ok(available);
    }


    /**
     * 根据课程id获取可选教师教师及时间
     * @return
     */
    @PostMapping("nin-arrange/getTeacherHouseORTime")
    public ResultModel getTeacherHouseORTime(Long courseId, Long teacherId, Long houseId) {
        List arrangeBo = ninArrangeService.getTeacherHouseORTime(courseId, teacherId, houseId);
        return ResultModel.ok(arrangeBo);

    }






}
