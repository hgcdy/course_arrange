package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinCareerCourse;
import cn.netinnet.coursearrange.mapper.NinCareerCourseMapper;
import cn.netinnet.coursearrange.mapper.NinCareerMapper;
import cn.netinnet.coursearrange.service.INinCareerCourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2022-09-08
 */
@Service
public class NinCareerCourseServiceImpl extends ServiceImpl<NinCareerCourseMapper, NinCareerCourse> implements INinCareerCourseService {

    @Autowired
    private NinCareerCourseMapper ninCareerCourseMapper;

    @Override
    public List<Map<String, Object>> getSelectList(Long careerId) {
        return ninCareerCourseMapper.getSelectList(careerId);
    }
}
