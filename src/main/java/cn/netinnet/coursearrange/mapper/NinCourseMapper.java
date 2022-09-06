package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.entity.NinCourse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

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
public interface NinCourseMapper extends BaseMapper<NinCourse> {

    List<NinCourse> getSelectList(NinCourse ninCourse);

    List<NinCourse> getAllList();


}
