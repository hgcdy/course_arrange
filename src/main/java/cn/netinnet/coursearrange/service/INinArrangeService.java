package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.bo.NinArrangeBo;
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

    void arrange();

    void empty();

    Map<String, String> getInfo(Long classId, Long teacherId, Long studentId, Integer count);

    Map<String, List<Map<String, Object>>> getLeisure(Long teacherId, String classIds, Long houseId, Integer houseType, Integer seatMin,Integer seatMax, Integer weekly);

    int addArrange(Integer weekly, Integer week, Integer pitchNum, Long houseId, Long teacherId, Long courseId, String classIdList);

    Map<String, Object> getPageSelectList(NinArrangeBo bo, Integer page, Integer size);

    int delArrange(Long id);

    int alterArrange(NinArrange arrange);


    /**
     * 根据课程id获取可选教室教师或根据教室教师获取时间
     * @return
     */
     List<Object> getTeacherHouseORTime(Long courseId, Long teacherId, Long houseId);


    /**
     * 给选修课程添加教室教师时的选择
     * @param id
     * @return
     */
     Map<String, List> getAvailable(Long id, Long teacherId, Long houseId, Integer week, Integer pitchNum);

    /**
     * 导出，
     * @param type 0-班级， 1-教师， 2-学生
     * @param id
     */
     void exportCourseForm(String type, Long id, HttpServletRequest request, HttpServletResponse response);

}
