package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinCareerCourseService;
import com.alibaba.fastjson.JSON;
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
 * @since 2022-09-08
 */
@RestController
@RequestMapping("/nin-career-course")
public class NinCareerCourseController {

    @Autowired
    private INinCareerCourseService ninCareerCourseService;

    @GetMapping("")
    public ModelAndView gotoTeacherCourse(Long careerId) {
        ModelAndView modelAndView = new ModelAndView("view/careerAdminView");
        return modelAndView;
    }

    /**
     * 根据专业id选择查询
     * @param careerId
     * @return
     */
    @PostMapping("/getSelectList")
    public ResultModel getSelectList(Long careerId) {
        List<Map<String, Object>> list = ninCareerCourseService.getSelectList(careerId);
        return ResultModel.ok(list);
    }

    @PostMapping("/addBatchCourse")
    public ResultModel addBatchCourse(String careerIds, String courseIds) {
        List<Long> careerIdList = JSON.parseArray(careerIds, Long.class);
        List<Long> courseIdList = JSON.parseArray(courseIds, Long.class);
        ninCareerCourseService.addBatchCourse(careerIdList, courseIdList);
        return ResultModel.ok();
    }

    @PostMapping("/delCareerCourse")
    public ResultModel delCareerCourse(Long id) {
        int i = ninCareerCourseService.delCareerCourse(id);
        if (i > 0) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

//    /**
//     * 根据专业id获取该专业的课程列表
//     * @param careerId
//     * @return
//     */
//    @PostMapping("/getCareerCourse")
//    public ResultModel getCareerCourse(Long careerId) {
//
//        return null;
//    }

}
