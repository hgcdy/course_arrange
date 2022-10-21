package cn.netinnet.coursearrange.controller;


import cn.netinnet.coursearrange.service.INinTeachClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wangjs
 * @since 2022-09-08
 */
@RestController
@RequestMapping("/nin-teach-class")
public class NinTeachClassController {
    @Autowired
    private INinTeachClassService ninTeachClassService;

}
