package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.entity.NinCareerCourse;
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
 * @since 2022-09-08
 */
@Mapper
public interface NinCareerCourseMapper extends BaseMapper<NinCareerCourse> {

    List<Map<String, Object>> getSelectList(Long careerId);

    void addBatchCourse(@Param("list") List<NinCareerCourse> ninCareerCourseList);
}
