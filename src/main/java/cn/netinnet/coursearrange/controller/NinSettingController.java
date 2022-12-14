package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.bo.SettingBo;
import cn.netinnet.coursearrange.global.ResultEntry;
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
     * @param state
     * @param courseName
     * @return
     */
    @GetMapping("/getSelectList")
    public ResultEntry getSelectList(String userType, String state, String courseName) {
        List<SettingBo> list = ninSettingService.getSelectList(userType, state , courseName);
        return ResultEntry.ok(list);
    }

    /**
     * 修改开放选课
     * @param settingIds
     * @param openState
     * @param openTime
     * @param closeTime
     * @return
     */
    @PostMapping("/alterBatch")
    public ResultEntry alterBatch(String settingIds, Integer openState, String openTime, String closeTime) {
            return ninSettingService.alterBatch(settingIds, openState, openTime, closeTime);
    }



}
