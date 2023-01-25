package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinCareer;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinCareerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


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
     * 跳转班级页面
     * @return
     */
    @GetMapping("")
    public ModelAndView gotoView() {
        return new ModelAndView("view/careerView");
    }


    /**
     * 获取学院名称列表
     * @return
     */
    @PostMapping("getCollegeList")
    public ResultModel getCollege() {
        return ResultModel.ok(ninCareerService.getCollegeList());
    }

    /**
     * 根据学院获取专业列表
     *
     * @return
     */
    @PostMapping("getCareerList")
    public ResultModel getCareer(String college) {
        return ResultModel.ok(ninCareerService.getNinCareerList(college));
    }


    /**
     * 获取专业列表，并按学院分组
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
        ninCareerService.alterSingle(ninCareer);
        return ResultModel.ok();
    }

    @PostMapping("alterCollege")
    public ResultModel alterCollege(String oldCollege, String newCollege) {
        int count = ninCareerService.count(new LambdaQueryWrapper<NinCareer>().eq(NinCareer::getCollege, newCollege));
        if (count == 0) {
            ninCareerService.update(new LambdaUpdateWrapper<NinCareer>()
                    .eq(NinCareer::getCollege, oldCollege)
                    .set(NinCareer::getCollege, newCollege));
            return ResultModel.ok();
        } else {
            return ResultModel.error(412, "重名");
        }
    }


}
