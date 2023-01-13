package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.bo.ArrangeBo;
import cn.netinnet.coursearrange.bo.HouseApplyBo;
import cn.netinnet.coursearrange.bo.HouseBo;
import cn.netinnet.coursearrange.entity.NinArrange;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
public interface INinArrangeService extends IService<NinArrange> {
    /**
     * 自动排课
     */
    void arrange();
    /**
     * 清空排课记录
     */
    void empty();
    /**
     * 获取课程表信息
     * @param count 周次（为空则获取整个学期的课程表）
     */
    Map<String, StringBuffer> getInfo(Long classId, Long teacherId, Long studentId, Integer count);
    /**
     * 补课，获取时间
     * @param bo
     * @return
     */
    List<String> getHouseApplyTime(HouseApplyBo bo);

    /**
     * 添加·排课信息
     */
    int addArrange(Integer weekly, Integer week, Integer pitchNum, Long houseId, Long teacherId, Long courseId, String classIdList);
    /**
     * 分页条件查询
     */
    Map<String, Object> getPageSelectList(ArrangeBo bo, Integer page, Integer size);
    /**
     * 根据id删除排课记录
     */
    int delArrange(Long id);
    /**
     * 导出，
     * @param type class-班级， teacher-教师， student-学生
     */
     void exportCourseForm(String type, Long id, Integer count, HttpServletRequest request, HttpServletResponse response);
}
