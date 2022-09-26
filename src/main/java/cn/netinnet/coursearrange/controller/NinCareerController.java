package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinCareer;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinCareerService;
import cn.netinnet.coursearrange.service.INinCourseService;
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
@RequestMapping("/nin-career")
@RequiresRoles(value = {"admin"}, logical = Logical.OR)
public class NinCareerController {

    @Autowired
    private INinCareerService ninCareerService;

    /**
     * 获取学院列表
     *
     * @return
     */
    @PostMapping("getCollegeList")
    public ResultModel getCollege() {
        return ResultModel.ok(ninCareerService.getCollegeList());
    }

    /**
     * 获取专业列表
     *
     * @return
     */
    @PostMapping("getCareerList")
    public ResultModel getCareer(String college) {
        return ResultModel.ok(ninCareerService.getNinCareerList(college));
    }


    /**
     * 获取学院专业列表
     * @return
     */
    @PostMapping("getCollegeCareerList")
    public ResultModel getCollegeCareerList() {
        return ResultModel.ok(ninCareerService.getCollegeCareerList());
    }



    /**
     * 增加专业
     * @param ninCareer
     * @return
     */
    @PostMapping("addCareer")
    public ResultModel addCareer(NinCareer ninCareer) {

        return ResultModel.ok(ninCareerService.addSingle(ninCareer));
    }

    /**
     * 删除专业
     * @param id
     * @return
     */
    @PostMapping("delCareer")
    public ResultModel delCareer(Long id) {
        return ResultModel.ok(ninCareerService.delById(id));
    }

    /**
     * 修改专业
     * @param ninCareer
     * @return
     */
    @PostMapping("alterCareer")
    public ResultModel alterCareer(NinCareer ninCareer) {
        return ResultModel.ok(ninCareerService.alterSingle(ninCareer));
    }


}
