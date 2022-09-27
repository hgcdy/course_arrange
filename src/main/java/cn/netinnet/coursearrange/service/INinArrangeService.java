package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.bo.NinArrangeBo;
import cn.netinnet.coursearrange.entity.NinArrange;
import cn.netinnet.coursearrange.model.ResultModel;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PostMapping;

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

    Map<String, String> getInfo(Long classId, Long teacherId, Long studentId, Integer count);

    Map<String, List<Map<String, Object>>> getLeisure(Long teacherId, String classIds, Long houseId, Integer houseType, Integer seatMin,Integer seatMax, Integer weekly);

    int addArrange(Integer weekly, Integer week, Integer pitchNum, Long houseId, Long teacherId, Long courseId, String classIdList);

    Map<String, Object> getPageSelectList(NinArrangeBo bo, Integer page, Integer size);

    int delArrange(Long id);

    int alterArrange(NinArrangeBo bo);


    /**
     * 根据课程id获取可选教室教师或根据教室教师获取时间
     * @return
     */
     List getTeacherHouseORTime(Long courseId, Long teacherId, Long houseId);



}
