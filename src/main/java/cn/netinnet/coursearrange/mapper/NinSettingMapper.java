package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.bo.SettingBo;
import cn.netinnet.coursearrange.entity.NinSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangjs
 * @since 2022-10-19
 */
@Mapper
public interface NinSettingMapper extends BaseMapper<NinSetting> {
    /**
     * 修改
     */
    void alterBatch(@Param("list") List<Long> settingIdList, @Param("openState") Integer openState, @Param("openTime") LocalDateTime openTime, @Param("closeTime") LocalDateTime closeTime);
}
