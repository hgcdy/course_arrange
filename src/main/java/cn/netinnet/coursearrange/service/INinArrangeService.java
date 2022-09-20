package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinArrange;
import com.baomidou.mybatisplus.extension.service.IService;

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

    List<Map<String, Object>> getLeisure(Long teacherId, String classIds, Long houseId, Integer houseType, Integer seatMin,Integer seatMax, Integer weekly, Integer week, Integer pitchNum);

}
