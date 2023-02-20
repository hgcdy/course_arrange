package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinCareerCourseService;
import com.alibaba.fastjson.JSON;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
    @GetMapping("/getSelectList")
    public ResultModel getSelectList(Long careerId) {
        return ResultModel.ok(ninCareerCourseService.getSelectList(careerId));
    }

    /**
     * 批量给多个专业批量添加课程
     * @param careerIds
     * @param courseIds
     * @return
     */
    @PostMapping("/addBatchCourse")
    public ResultModel addBatchCourse(String careerIds, String courseIds) {
        List<Long> careerIdList = JSON.parseArray(careerIds, Long.class);
        List<Long> courseIdList = JSON.parseArray(courseIds, Long.class);
        ninCareerCourseService.addBatchCourse(careerIdList, courseIdList);
        return ResultModel.ok();
    }

    /**
     * 根据id删除记录
     * @param id
     * @return
     */
    @PostMapping("/delCareerCourse")
    public ResultModel delCareerCourse(Long id) {
        boolean b = ninCareerCourseService.delCareerCourse(id);
        if (b) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }


}
