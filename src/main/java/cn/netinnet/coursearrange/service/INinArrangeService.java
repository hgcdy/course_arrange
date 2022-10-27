package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.bo.ArrangeBo;
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
     * @param classId
     * @param teacherId
     * @param studentId
     * @param count 周次（为空则获取整个学期的课程表）
     * @return
     */
    Map<String, String> getInfo(Long classId, Long teacherId, Long studentId, Integer count);

    /**
     * 补课-获取空闲资源（教室申请）
     * @param teacherId
     * @param classIds
     * @param houseId
     * @param houseType
     * @param seatMin
     * @param seatMax
     * @param weekly
     * @return
     */
    Map<String, List<HouseBo>> getLeisure(Long teacherId, String classIds, Long houseId, Integer houseType, Integer seatMin, Integer seatMax, Integer weekly);

    /**
     * 添加·排课信息
     * @param weekly
     * @param week
     * @param pitchNum
     * @param houseId
     * @param teacherId
     * @param courseId
     * @param classIdList
     * @return
     */
    int addArrange(Integer weekly, Integer week, Integer pitchNum, Long houseId, Long teacherId, Long courseId, String classIdList);

    /**
     * 分页条件查询
     * @param bo
     * @param page
     * @param size
     * @return
     */
    Map<String, Object> getPageSelectList(ArrangeBo bo, Integer page, Integer size);

    /**
     * 根据id删除排课记录
     * @param id
     * @return
     */
    int delArrange(Long id);

    /**
     * 修改排课记录
     * @param arrange
     * @return
     */
    int alterArrange(NinArrange arrange);

    /**
     * 根据课程id获取可选教室教师或根据教室教师获取时间
     * @return
     */
     List<Object> getTeacherHouseORTime(Long courseId, Long teacherId, Long houseId);

    /**
     * 导出，
     * @param type 0-班级， 1-教师， 2-学生
     * @param id
     */
     void exportCourseForm(String type, Long id, HttpServletRequest request, HttpServletResponse response);

}
