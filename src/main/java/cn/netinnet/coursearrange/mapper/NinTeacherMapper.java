package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.entity.NinTeacher;
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
public interface NinTeacherMapper extends BaseMapper<NinTeacher> {

    /**
     * 条件查询
     * @param teacherName
     * @return
     */
    List<Map<String, Object>> getSelectList(String teacherName);

    /**
     * 根据课程查询教师
     * @param courseId
     * @return
     */
    List<NinTeacher> getSelectByCourse(@Param("courseId") Long courseId);

}
