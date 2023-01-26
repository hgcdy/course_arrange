package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.bo.StudentBo;
import cn.netinnet.coursearrange.entity.NinStudent;
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
public interface NinStudentMapper extends BaseMapper<NinStudent> {
    /**
     * 分页条件查询
     */
    List<StudentBo> getSelectList(@Param("careerIds") List<Long> careerIds, @Param("classId") Long classId, @Param("studentName") String studentName);

}
