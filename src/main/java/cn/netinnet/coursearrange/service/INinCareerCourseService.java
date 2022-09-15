package cn.netinnet.coursearrange.service;

import cn.netinnet.coursearrange.entity.NinCareerCourse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangjs
 * @since 2022-09-08
 */
public interface INinCareerCourseService extends IService<NinCareerCourse> {

    List<Map<String, Object>> getSelectList(Long careerId);

}
