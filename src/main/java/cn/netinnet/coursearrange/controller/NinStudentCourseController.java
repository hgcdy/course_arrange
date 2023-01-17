package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.entity.NinStudentCourse;
import cn.netinnet.coursearrange.entity.NinTeacherCourse;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@RestController
@RequestMapping("/nin-student-course")
@RequiresRoles(value = {"admin", "student"}, logical = Logical.OR)
public class NinStudentCourseController {
    @Autowired
    private INinStudentCourseService ninStudentCourseService;

    Lock lock = new ReentrantLock();

    /**
     * 跳转学生-课程页面
     */
    @GetMapping("")
    public ModelAndView gotoStudentCourse(){
        return new ModelAndView("view/selectCourseView");
    }

    @PostMapping("/getCourse")
    public ResultModel getSelectCourse(Long id) {
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", i);
            map.put("name", "xx" + i);
            maps.add(map);
        }
        ArrayList<Map<String, Object>> maps1 = new ArrayList<>();
        for (int i = 0; i > -10 ; i--) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", i);
            map.put("name", "xx" + i);
            maps1.add(map);
        }

        HashMap<String, List> stringListHashMap = new HashMap<>();
        stringListHashMap.put("selected", maps);
        stringListHashMap.put("unselected", maps1);

        return ResultModel.ok(stringListHashMap);
    }

    /**
     * 添加记录
     * @return
     */
    @PostMapping("/addCourse")
    public ResultModel addStudent(Long id, Long courseId){
        try {
            lock.lock();
            boolean b = ninStudentCourseService.save(new NinStudentCourse() {{
                setTakeClassId(1L);
                setStudentId(id);
                setCourseId(courseId);
            }});
            return b ? ResultModel.ok() : ResultModel.error(412, "新增失败");
        } finally {
            lock.unlock();
        }
    }

    /**
     * 删除记录
     * @param id
     * @return
     */
    @PostMapping("/delCourse")
    public ResultModel delStudent(Long id, Long courseId){
        try {
            lock.lock();
            boolean b = ninStudentCourseService.remove(new LambdaQueryWrapper<NinStudentCourse>().eq(NinStudentCourse::getStudentId, id).eq(NinStudentCourse::getCourseId, courseId));
            return b ? ResultModel.ok() : ResultModel.error(412, "删除失败");
        } finally {
            lock.unlock();
        }
    }


}
