package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.entity.NinTeacherCourse;
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
public interface NinTeacherCourseMapper extends BaseMapper<NinTeacherCourse> {
    /**
     * 查询
     */
    List<ContactCourseBo> getSelectList(Long teacherId);
}
