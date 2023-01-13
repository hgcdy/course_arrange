package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinHouse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
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
     */
    Map<String, Object> getPageSelectList(Integer page, Integer size,  String houseName, Integer houseType, Integer firstSeat, Integer tailSeat);
    /**
     * 单个新增
     */
    int addSingle(NinHouse ninHouse);
    /**
     * 单个删除
     */
    int delById(Long id);
    /**
     * 单个修改
     */
    int alterSingle(NinHouse ninHouse);
    /**
     * 单条查询
     */
    NinHouse getHouseById(Long id);
    /**
     * 根据类型查询教室
     */
    List<NinHouse> getHouseByType(Integer houseType);
}
