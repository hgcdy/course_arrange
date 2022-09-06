package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.entity.NinClassCourse;
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
public interface NinClassCourseMapper extends BaseMapper<NinClassCourse> {

    List<Map<String, Object>> getSelectList(Long classId);

    int addBatch(@Param("list") List<NinClassCourse> ninClassCourseList);

    List<Map<String, Object>> sameVerify(@Param("classIdList") List<Long> classIdList,
                                         @Param("courseIdList") List<Long> courseIdList);

}
