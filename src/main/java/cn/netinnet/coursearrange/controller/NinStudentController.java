package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.mapper.NinStudentMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinStudentService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import java.util.List;
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
public class NinStudentController {

    @Autowired
    private INinStudentService ninStudentService;

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
    public ResultModel getPageSelectList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         String career, Long classId, String studentName){
        Map<String, Object> map = ninStudentService.getPageSelectList(page, size, career, classId, studentName);
        return ResultModel.ok(map);
    }

    /**
     * 专业班级列表
     * @return
     */
    @PostMapping("/getCareerClassList")
    public ResultModel getCareerClassList(){
        Map<String, List<NinClass>> map = ninStudentService.getCareerClassList();
        return ResultModel.ok(map);
    }

    /**
     * 新增
     * @param ninStudent
     * @return
     */
    @PostMapping("/addStudent")
    public ResultModel addStudent(NinStudent ninStudent){
        int i = ninStudentService.addSingle(ninStudent);
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
    @PostMapping("/delStudent")
    public ResultModel delStudent(Long id){
        int i = ninStudentService.delById(id);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 批量删除
     * @param Ids
     * @return
     */
    @PostMapping("/delBatchStudent")
    public ResultModel delBatchStudent(String Ids){
        List<Long> studentIdList = JSON.parseArray(Ids, Long.class);
        int i = ninStudentService.delBatch(studentIdList);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 修改
     * @param ninStudent
     * @return
     */
    @PostMapping("alterStudent")
    public ResultModel alterStudent(NinStudent ninStudent){
        int i = ninStudentService.alterSingle(ninStudent);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "修改失败");
    }

    @PostMapping("/getStudentById")
    public ResultModel getStudentById(Long id){
        return ResultModel.ok(ninStudentService.getStudentById(id));
    }



}
