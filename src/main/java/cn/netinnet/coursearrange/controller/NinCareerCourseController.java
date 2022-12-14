package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.enums.ResultEnum;
import cn.netinnet.coursearrange.global.ResultEntry;
import cn.netinnet.coursearrange.service.INinCareerCourseService;
import com.alibaba.fastjson.JSON;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangjs
 * @since 2022-09-08
 */
@RestController
@RequestMapping("/nin-career-course")
@RequiresRoles(value = {"admin"}, logical = Logical.OR)
public class NinCareerCourseController {

    @Autowired
    private INinCareerCourseService ninCareerCourseService;

    /**
     * 根据专业id选择查询
     * @param careerId
     * @return
     */
    @PostMapping("/getSelectList")
    public ResultEntry getSelectList(Long careerId) {
        List<ContactCourseBo> list = ninCareerCourseService.getSelectList(careerId);
        return ResultEntry.ok(list);
    }

    /**
     * 批量给多个专业批量添加课程
     * @param careerIds
     * @param courseIds
     * @return
     */
    @PostMapping("/addBatchCourse")
    public ResultEntry addBatchCourse(String careerIds, String courseIds) {
        List<Long> careerIdList = JSON.parseArray(careerIds, Long.class);
        List<Long> courseIdList = JSON.parseArray(courseIds, Long.class);
        ninCareerCourseService.addBatchCourse(careerIdList, courseIdList);
        return ResultEntry.ok();
    }

    /**
     * 根据id删除记录
     * @param id
     * @return
     */
    @PostMapping("/delCareerCourse")
    public ResultEntry delCareerCourse(Long id) {
        int i = ninCareerCourseService.delCareerCourse(id);
        if (i > 0) {
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }


}
