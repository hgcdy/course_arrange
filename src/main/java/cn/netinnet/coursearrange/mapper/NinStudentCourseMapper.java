package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.entity.NinStudentCourse;
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
 * @since 2022-08-18
 */
@Mapper
public interface NinStudentCourseMapper extends BaseMapper<NinStudentCourse> {
    /**
     * 根据学生id批量删除
     */
    int delBatchStudentId(@Param("list") List<Long> studentIdList);
    /**
     * 根据学生id批量获取
     */
    List<NinStudentCourse> getStudentIds(@Param("list") List<Long> studentIdList);
    /**
     * 查询
     */
    List<ContactCourseBo> getSelectList(Long Student);
}
