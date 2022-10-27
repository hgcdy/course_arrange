package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.bo.CourseBo;
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
    /**
     * 课程查询
     */
    List<CourseBo> getSelectList(NinCourse ninCourse);
    /**
     * 获取去除被选的选修课程后的全部课程
     */
    List<NinCourse> reSelectCourse();
}
