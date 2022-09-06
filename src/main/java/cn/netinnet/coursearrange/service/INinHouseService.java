package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinCourse;
import cn.netinnet.coursearrange.entity.NinHouse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
public interface INinHouseService extends IService<NinHouse> {

    /**
     * 分页条件查询
     * @param page 页码
     * @param size 页数
     * @param ninHouse 实体类
     * @return
     */
    Map<String, Object> getPageSelectList(Integer page, Integer size,  String houseName, Integer houseType, Integer firstSeat, Integer tailSeat);

    /**
     * 单个新增
     * @param ninHouse
     * @return
     */
    int addSingle(NinHouse ninHouse);


    /**
     * 单个删除
     * @param id
     * @return
     */
    int delById(Long id);

    /**
     * 单个修改
     * @param ninHouse
     * @return
     */
    int alterSingle(NinHouse ninHouse);

    /**
     * 单条查询
     * @param id
     * @return
     */
    NinHouse getHouseById(Long id);
}
