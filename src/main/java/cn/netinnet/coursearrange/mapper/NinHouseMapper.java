package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.entity.NinHouse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    List<NinHouse> getSelectList(@Param("houseName") String houseName, @Param("houseType")Integer houseType, @Param("firstSeat")Integer firstSeat, @Param("tailSeat")Integer tailSeat);

}
