package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinCareer;
import cn.netinnet.coursearrange.mapper.NinCareerMapper;
import cn.netinnet.coursearrange.service.INinCareerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2022-09-08
 */
@Service
public class NinCareerServiceImpl extends ServiceImpl<NinCareerMapper, NinCareer> implements INinCareerService {

    @Autowired
    private NinCareerMapper ninCareerMapper;

    @Override
    public List<NinCareer> getNinCareerList(String college) {
        List<NinCareer> ninCareerList = ninCareerMapper.getNinCareerList(college);
        return ninCareerList;
    }
}
