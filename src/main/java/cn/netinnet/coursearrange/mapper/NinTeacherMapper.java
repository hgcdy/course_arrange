package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.bo.TeacherBo;
import cn.netinnet.coursearrange.entity.NinTeacher;
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
public interface NinTeacherMapper extends BaseMapper<NinTeacher> {
    /**
     * 条件查询
     */
    List<TeacherBo> getSelectList(String teacherName);
}
