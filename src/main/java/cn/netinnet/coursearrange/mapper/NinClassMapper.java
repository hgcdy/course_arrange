package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.bo.ClassBo;
import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.entity.NinCourse;
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
     */
    List<ClassBo> getSelectList(@Param("college") String college,
                                @Param("careerId") Long careerId,
                                @Param("className") String className);
    /**
     * 根据班级id列表批量给课程数量+1
     */
    int addBatchCourseNum(@Param("list") List<Long> classIdList);
    /**
     * 根据班级id列表批量给课程数量-1
     */
    int subBatchCourseNum(@Param("list") List<Long> classIdList);
    /**
     * 班级人数+1
     */
    int addPeopleNum(Long id);
    /**
     * 班级人数-1
     */
    int subPeopleNum(Long id);
    /**
     * 学院专业班级列表
     */
    List<ClassBo> collegeCareerClassList();
    /**
     * 根据条件获取班级列表
     */
    List<ClassBo> getClassList(@Param("college") String college,
                               @Param("careerId") Long careerId);
    /**
     * 批量修改班级的课程记录
     * @param list 减少时，courseNum为负
     * [{careerId:xx,courseNum:1},{careerId:xx,courseNum:-1}]
     */
    int alterBatchPeopleNum(@Param("list") List<Map<String, Object>> list);
    /**
     * 批量修改班级的课程记录
     * @param list 减少时，courseNum为负
     * [{careerId:xx,courseNum:1},{careerId:xx,courseNum:-1}]
     */
    int alterBatchCourseNum(@Param("list") List<Map<String, Object>> list);
    /**
     * 根据班级id获取该班级选择的课程
     */
    List<NinCourse> getCourseList(Long classId);
}
