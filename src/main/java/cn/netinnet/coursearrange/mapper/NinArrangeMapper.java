package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.bo.NinArrangeBo;
import cn.netinnet.coursearrange.entity.NinArrange;
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
public interface NinArrangeMapper extends BaseMapper<NinArrange> {

    /**
     * 排课记录批量插入
     * @param ninArrangeList
     */
    void addBatch(@Param("list") List<NinArrange> ninArrangeList);

    /**
     * 清空记录
     */
    void empty();


    /**
     * 符合条件的课程表信息
     * @param classIdList
     * @param teachClassIdList
     * @param teacherId
     * @return
     */
    List<Map<String, Object>> getInfo(@Param("classIdList") List<Long> classIdList,
                                      @Param("teachClassIdList") List<Long> teachClassIdList,
                                      @Param("teacherId") Long teacherId);

    /**
     * 符合条件的排课信息列表
     * @param bo
     * @return
     */
    List<Map<String, Object>> getSelectList(NinArrangeBo bo);


    /**
     * 返回字段有为空的数据
     * @param must
     * @return
     */
    List<Map<String, Object>> getSelectNull(@Param("must") Integer must);

    /**
     * 返回可选资源
     * @param teacherId
     * @param houseId
     * @return
     */
    List<NinArrange> getAvailable(@Param("teacherId") Long teacherId,
                                  @Param("houseId") Long houseId);

    void updateNullById(Long id);
}
