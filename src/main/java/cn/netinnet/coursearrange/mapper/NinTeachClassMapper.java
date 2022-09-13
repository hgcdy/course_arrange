package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.entity.NinTeachClass;
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
 * @since 2022-09-08
 */
@Mapper
public interface NinTeachClassMapper extends BaseMapper<NinTeachClass> {

    void addBatch(@Param("list") List<NinTeachClass> ninTeachClassList);

    List<Long> getTeachClassIdList(@Param("classId") Long classId);

}
