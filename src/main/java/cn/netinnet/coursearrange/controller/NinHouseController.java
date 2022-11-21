package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinHouse;
import cn.netinnet.coursearrange.enums.ResultEnum;
import cn.netinnet.coursearrange.global.ResultEntry;
import cn.netinnet.coursearrange.service.INinHouseService;
import com.sun.istack.internal.NotNull;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
@RequestMapping("/nin-house")
@RequiresRoles(value = {"admin"}, logical = Logical.OR)
public class NinHouseController {

    @Autowired
    private INinHouseService ninHouseService;

    //跳转教室页面
    @GetMapping("")
    public ModelAndView gotoView(){
        return new ModelAndView("view/houseView");
    }


    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param houseName
     * @param houseType
     * @param firstSeat
     * @param tailSeat
     * @return
     */
    @PostMapping("/getPageSelectList")
    public ResultEntry getSelectPageList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         String houseName, Integer houseType, Integer firstSeat, Integer tailSeat){
        Map<String, Object> map = ninHouseService.getPageSelectList(page, size, houseName, houseType, firstSeat, tailSeat);
        return ResultEntry.ok(map);
    }

    /**
     * 添加教室
     * @param ninHouse
     * @return
     */
    @PostMapping("/addHouse")
    public ResultEntry addHouse(NinHouse ninHouse){
        int i = ninHouseService.addSingle(ninHouse);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 删除教室
     * @param id
     * @return
     */
    @PostMapping("/delHouse")
    public ResultEntry delHouse(Long id){
        int i = ninHouseService.delById(id);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 编辑教室
     * @param ninHouse
     * @return
     */
    @PostMapping("/alterHouse")
    public ResultEntry alterHouse(NinHouse ninHouse){
        int i = ninHouseService.alterSingle(ninHouse);
        if (i > 0){
            return ResultEntry.ok();
        }
        return ResultEntry.error(ResultEnum.FAILURE);
    }

    /**
     * 根据id获取单条记录
     * @param id
     * @return
     */
    @GetMapping("/getHouseById")
    public ResultEntry getHouseById(@NotNull Long id){
        return ResultEntry.ok(ninHouseService.getHouseById(id));
    }

}
