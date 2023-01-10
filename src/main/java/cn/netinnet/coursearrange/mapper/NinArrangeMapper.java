package cn.netinnet.coursearrange.mapper;

import cn.netinnet.coursearrange.bo.ArrangeBo;
import cn.netinnet.coursearrange.entity.NinArrange;
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
public interface NinArrangeMapper extends BaseMapper<NinArrange> {
    /**
     * 排课记录批量插入
     */
    void addBatch(@Param("list") List<NinArrange> ninArrangeList);
    /**
     * 符合条件的课程表信息
     */
    List<ArrangeBo> getInfo(@Param("classIdList") List<Long> classIdList,
                            @Param("teachClassIdList") List<Long> teachClassIdList,
                            @Param("teacherId") Long teacherId);
    /**
     * 符合条件的排课信息列表
     */
    List<ArrangeBo> getSelectList(ArrangeBo bo);
    /**
     * 删除教师选课时的置空
     */
    void updateNullById(@Param("id") Long id);
    /**
     * 判断是否可以插入 返回数大于0即不可插入
     * @param weekly 第几周（sql里表示0每周，1-单周，2-双周）
     */
    Integer getArrangeVerify(@Param("list") List<Long> classIdList, @Param("weekly") Integer weekly,
                             @Param("week") Integer week, @Param("pitchNum") Integer pitchNum,
                             @Param("houseId") Long houseId, @Param("teacherId") Long teacherId);
}
