package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinClassService;
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
@RequestMapping("/nin-class")
public class NinClassController {
    @Autowired
    private INinClassService ninClassService;

    @GetMapping("")
    public ModelAndView gotoView(){
        return new ModelAndView("view/classView");
    }

    /**
     * 条件分页查询
     * @param page
     * @param size
     * @param career 专业,"0"则为选修
     * @return
     */
    @PostMapping("/getPageSelectList")
    public ResultModel getPageSelectList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                 Long careerId){
        Map<String, Object> map = ninClassService.getPageSelectList(page, size, careerId, null);
        return ResultModel.ok(map);
    }

    /**
     * 专业列表
     * @return
     */
    @GetMapping("/careerList")
    public ResultModel careerList(){
        return ResultModel.ok(ninClassService.careerList());
    }

    /**
     * 专业班级
     * @return
     */
    @GetMapping("/careerClassList")
    public ResultModel careerClassList(){
        return ResultModel.ok(ninClassService.careerClassList());
    }

    /**
     * 新增
     * @param ninClass
     * @return
     */
    @PostMapping ("/addClass")
    public ResultModel addClass(NinClass ninClass){
        int i = ninClassService.addSingle(ninClass);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "新增失败");
    }

    /**
     * 删除班级
     * @param id 班级id
     * @return
     */
    @PostMapping("/delClass")
    public ResultModel delClass(Long id){
        int i = ninClassService.delById(id);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 删除该班级的学生及记录
     * @param id 班级id
     * @return
     */
    @PostMapping("/delClassStudent")
    public ResultModel delClassStudent(Long id){
        int i = ninClassService.delBatchStudent(id);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 修改
     * @param ninClass
     * @return
     */
    @PostMapping("/alterClass")
    public ResultModel alterClass(NinClass ninClass){
        int i = ninClassService.alterSingle(ninClass);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "修改失败");
    }

}
