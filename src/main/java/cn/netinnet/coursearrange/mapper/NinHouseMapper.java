package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.bo.HouseBo;
import cn.netinnet.coursearrange.entity.NinHouse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@Mapper
public interface NinHouseMapper extends BaseMapper<NinHouse> {
    /**
     * 教室查询
     * @param houseName 模糊查询教室名称
     * @param houseType 查询教室类型
     * @param firstSeat 座位范围（min）
     * @param tailSeat 座位（max）
     * @return
     */
    List<HouseBo> getSelectList(@Param("houseName") String houseName, @Param("houseType")Integer houseType, @Param("firstSeat")Integer firstSeat, @Param("tailSeat")Integer tailSeat);
}
