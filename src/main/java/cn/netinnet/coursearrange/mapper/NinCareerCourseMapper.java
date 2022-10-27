package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.bo.ContactCourseBo;
import cn.netinnet.coursearrange.entity.NinCareerCourse;
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
public interface NinCareerCourseMapper extends BaseMapper<NinCareerCourse> {

    /**
     * 条件查询
     * @param careerId
     * @return
     */
    List<ContactCourseBo> getSelectList(Long careerId);

    /**
     * 多专业多课程批量添加
     * @param ninCareerCourseList
     */
    void addBatchCourse(@Param("list") List<NinCareerCourse> ninCareerCourseList);
}
