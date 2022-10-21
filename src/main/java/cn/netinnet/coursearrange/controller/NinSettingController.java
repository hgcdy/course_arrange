package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.NinSettingBo;
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

    //获取设置信息
    @GetMapping("/getSelectList")
    public ResultModel getSelectList(String userType, String state, String courseName) {
        List<NinSettingBo> list = ninSettingService.getSelectList(userType, state , courseName);
        return ResultModel.ok(list);
    }

    //修改设置信息
    @PostMapping("/alterBatch")
    public ResultModel alterBatch(String settingIds, Integer openState, String openTime, String closeTime) {
            return ninSettingService.alterBatch(settingIds, openState, openTime, closeTime);
    }



}
