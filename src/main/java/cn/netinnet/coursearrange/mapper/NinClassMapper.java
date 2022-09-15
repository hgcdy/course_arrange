package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.entity.NinClass;
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
public interface NinClassMapper extends BaseMapper<NinClass> {

    /**
     * 条件查询列表
     * @param careerId
     * @return
     */
    List<Map<String, Object>> getSelectList(@Param("college") String college, @Param("careerId") Long careerId, @Param("className") String className);

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

    /**
     * 班级人数+1
     * @param id
     * @return
     */
    int addPeopleNum(Long id);

    /**
     * 班级人数-1
     * @param id
     * @return
     */
    int subPeopleNum(Long id);

}
