package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.entity.NinArrange;
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
public interface NinArrangeMapper extends BaseMapper<NinArrange> {

    void addBatch(@Param("list") List<NinArrange> ninArrangeList);

    List<Map<String, Object>> getInfo(@Param("classIdList") List<Long> classIdList, @Param("classesIdList") List<Long> classesIdList, @Param("teacherId") Long teacherId);

}
