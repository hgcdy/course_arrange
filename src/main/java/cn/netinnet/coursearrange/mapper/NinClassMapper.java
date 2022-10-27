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
     * @param careerId
     * @return
     */
    List<ClassBo> getSelectList(@Param("college") String college, @Param("careerId") Long careerId, @Param("className") String className);


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

    /**
     * 学院专业班级列表
     * @return
     */
    List<ClassBo> collegeCareerClassList();

    /**
     * 根据条件获取班级列表
     * @param college
     * @param careerId
     * @return
     */
    List<ClassBo> getClassList(@Param("college") String college, @Param("careerId") Long careerId);


    /**
     * 批量人数减一
     * @param ninClassList
     * @return
     */
    int subBatchPeopleNum(List<NinClass> ninClassList);


    /**
     * 批量修改班级的课程记录
     * @param list 减少时，courseNum为负
     * [{careerId:xx,courseNum:1},{careerId:xx,courseNum:-1}]
     * @return
     */
    int alterBatchCourseNum(@Param("list") List<Map<String, Object>> list);

    /**
     * 根据班级id获取该班级选择的课程
     * @param classId
     * @return
     */
    List<NinCourse> getCourseList(Long classId);

}
