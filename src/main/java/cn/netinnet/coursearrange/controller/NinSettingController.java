package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @GetMapping("")
    public ModelAndView gotoStudent(){
        return new ModelAndView("view/settingView");
    }


    @GetMapping("/getSelectList")
    public ResultModel getSelectList(String userType, String state, String courseName) {
        List<Map<String, Object>> list = ninSettingService.getSelectList(userType, state , courseName);
        return ResultModel.ok(list);
    }

    @PostMapping("/alterBatch")
    public ResultModel alterBatch(String settingIds, Integer openState, String openTime, String closeTime) {
        //todo 报错
        return ninSettingService.alterBatch(settingIds, openState,
                openTime == null ? null : LocalDateTime.parse(openTime),
                closeTime == null ? null : LocalDateTime.parse(closeTime));
    }



}
