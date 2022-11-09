package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.entity.NinCareer;
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
public interface NinCareerMapper extends BaseMapper<NinCareer> {
    /**
     * 获取专业列表
     */
    List<NinCareer> getNinCareerList(@Param("college") String college);
    /**
     * 该班级数量+1
     */
    void addClassNum(@Param("id") Long id);
    /**
     * 该班级数量+1
     */
    void subClassNum(@Param("id") Long id);
}
