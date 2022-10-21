package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.entity.NinHouse;
import cn.netinnet.coursearrange.model.ResultModel;
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


    //分页查询
    @PostMapping("/getPageSelectList")
    public ResultModel getSelectPageList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         String houseName, Integer houseType, Integer firstSeat, Integer tailSeat){
        Map<String, Object> map = ninHouseService.getPageSelectList(page, size, houseName, houseType, firstSeat, tailSeat);
        return ResultModel.ok(map);
    }

    /**
     * 添加教室
     * @param ninHouse
     * @return
     */
    @PostMapping("/addHouse")
    public ResultModel addHouse(NinHouse ninHouse){
        int i = ninHouseService.addSingle(ninHouse);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "新增失败");
    }

    /**
     * 删除教室
     * @param id
     * @return
     */
    @PostMapping("/delHouse")
    public ResultModel delHouse(Long id){
        int i = ninHouseService.delById(id);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "删除失败");
    }

    /**
     * 编辑教室
     * @param ninHouse
     * @return
     */
    @PostMapping("/alterHouse")
    public ResultModel alterHouse(NinHouse ninHouse){
        int i = ninHouseService.alterSingle(ninHouse);
        if (i > 0){
            return ResultModel.ok();
        }
        return ResultModel.error(412, "修改失败");
    }

    //根据id获取
    @GetMapping("/getHouseById")
    public ResultModel getHouseById(@NotNull Long id){
        return ResultModel.ok(ninHouseService.getHouseById(id));
    }

}
