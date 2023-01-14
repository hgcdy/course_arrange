package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinMessage;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinMessageService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
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
        return new ModelAndView("");
    }

    /**
     * 获取列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/getMsgList")
    public ResultModel getMsgList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                  @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return ResultModel.ok(ninMessageService.getMsgList(page, size));
    }

    /**
     * 批量删除
     * @param msgIds
     * @return
     */
    @PostMapping("/delBatchMsg")
    public ResultModel delBatchMsg(String msgIds) {
        List<Long> msgIdList = JSON.parseArray(msgIds, Long.class);
        boolean b = ninMessageService.delBatchMsg(msgIdList);
        if (b) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 批量已读
     * @param msgIds
     * @return
     */
    @PostMapping("/readBatchMag")
    public ResultModel readBatchMag(String msgIds) {
        List<Long> msgIdList = JSON.parseArray(msgIds, Long.class);
        boolean b = ninMessageService.readBatchMag(msgIdList);
        if (b) {
            return ResultModel.ok();
        }
        return ResultModel.error(412, "更新失败");
    }

}
