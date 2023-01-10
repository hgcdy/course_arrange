package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.SettingBo;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wangjs
 * @since 2022-10-19
 */
@RestController
@RequestMapping("/nin-setting")
public class NinSettingController {

    @Autowired
    private INinSettingService ninSettingService;

    //跳转设置页面
    @GetMapping("")
    public ModelAndView gotoStudent(){
        return new ModelAndView("view/settingView");
    }

    /**
     * 根据条件获取设置信息
     * @param userType
     * @param openState
     * @param courseName
     * @return
     */
    @GetMapping("/getSelectList")
    public ResultModel getSelectList(String userType, Integer openState, String courseName) {
        List<SettingBo> list = ninSettingService.getSelectList(userType, openState , courseName);
        return ResultModel.ok(list);
    }

    /**
     * 修改开放选课
     * @param settingIds
     * @param openTime
     * @param closeTime
     * @return
     */
    @PostMapping("/alterBatch")
    public ResultModel alterBatch(String settingIds, String userType, String openTime, String closeTime) {
            return ninSettingService.alterBatch(settingIds, userType, openTime, closeTime);
    }



}
