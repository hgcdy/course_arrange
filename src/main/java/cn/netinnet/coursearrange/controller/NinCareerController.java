package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinCareer;
import cn.netinnet.coursearrange.global.ResultEntry;
import cn.netinnet.coursearrange.service.INinCareerService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
     * 获取学院名称列表
     * @return
     */
    @PostMapping("getCollegeList")
    public ResultEntry getCollege() {
        return ResultEntry.ok(ninCareerService.getCollegeList());
    }

    /**
     * 根据学院获取专业列表
     *
     * @return
     */
    @PostMapping("getCareerList")
    public ResultEntry getCareer(String college) {
        return ResultEntry.ok(ninCareerService.getNinCareerList(college));
    }


    /**
     * 获取专业列表，并按学院分组
     * @return
     */
    @PostMapping("getCollegeCareerList")
    public ResultEntry getCollegeCareerList() {
        return ResultEntry.ok(ninCareerService.getCollegeCareerList());
    }

    /**
     * 增加专业
     * @param ninCareer
     * @return
     */
    @PostMapping("addCareer")
    public ResultEntry addCareer(NinCareer ninCareer) {
        return ResultEntry.ok(ninCareerService.addSingle(ninCareer));
    }

    /**
     * 删除专业
     * @param id
     * @return
     */
    @PostMapping("delCareer")
    public ResultEntry delCareer(Long id) {
        return ResultEntry.ok(ninCareerService.delById(id));
    }

    /**
     * 修改专业
     * @param ninCareer
     * @return
     */
    @PostMapping("alterCareer")
    public ResultEntry alterCareer(NinCareer ninCareer) {
        return ResultEntry.ok(ninCareerService.alterSingle(ninCareer));
    }


}
