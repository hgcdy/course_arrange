package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.entity.NinClass;
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
public interface NinClassMapper extends BaseMapper<NinClass> {

    /**
     * 条件查询列表
     * @param career
     * @return
     */
    List<NinClass> getSelectList(@Param("career") String career, @Param("className") String className);

    /**
     * 专业列表
     * @return
     */
    List<String> careerList();

    /**
     * 根据班级id列表批量给课程数量+1
     * @param classIdList
     * @return
     */
    int addBatchCourseNum(@Param("list") List<Long> classIdList);

    /**
     * 根据班级id列表批量给课程数量-1
     * @param classIdList
     * @return
     */
    int subBatchCourseNum(@Param("list") List<Long> classIdList);

    int addPeopleNum(Long id);

    int subPeopleNum(Long id);

}
