package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinMessageService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wangjs
 * @since 2023-01-14
 */
@RestController
@RequestMapping("/nin-message")
public class NinMessageController {

    @Autowired
    private INinMessageService ninMessageService;

    @GetMapping("")
    public ModelAndView gotoView() {
        return new ModelAndView("view/messageView");
    }

    /**
     * 获取列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/getMsgList")
    public ResultModel getMsgList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                  @RequestParam(value = "size", defaultValue = "5") Integer size) {
        return ResultModel.ok(ninMessageService.getMsgList(page, size));
    }

    /**
     * 删除
     * @return
     */
    @PostMapping("/delMsg")
    public ResultModel delMsg(Long id) {
        boolean b = ninMessageService.delMsg(id);
        if (b) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 已读
     * @return
     */
    @PostMapping("/readMag")
    public ResultModel readMag(Long id) {
        boolean b = ninMessageService.readMag(id);
        if (b) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "更新失败");
    }

    @PostMapping("consentMsg")
    public ResultModel consentMsg(Long id, Integer isConsent) {
        ninMessageService.consentMsg(id, isConsent);
        return null;
    }

}
