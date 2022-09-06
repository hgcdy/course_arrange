package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.entity.NinStudent;
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
public interface NinStudentMapper extends BaseMapper<NinStudent> {

    List<Map<String, Object>> getSelectList(@Param("list") List<Long> classIds, @Param("studentName") String studentName);

}
