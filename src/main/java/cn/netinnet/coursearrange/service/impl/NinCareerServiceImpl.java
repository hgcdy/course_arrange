package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinCareer;
import cn.netinnet.coursearrange.entity.NinCareerCourse;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinCareerCourseMapper;
import cn.netinnet.coursearrange.mapper.NinCareerMapper;
import cn.netinnet.coursearrange.service.INinCareerService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2022-09-08
 */
@Service
public class NinCareerServiceImpl extends ServiceImpl<NinCareerMapper, NinCareer> implements INinCareerService {

    @Autowired
    private NinCareerMapper ninCareerMapper;
    @Autowired
    private NinCareerCourseMapper ninCareerCourseMapper;

    @Override
    public List<String> getCollegeList() {
        return ninCareerMapper.getCollegeList();
    }

    @Override
    public List<NinCareer> getNinCareerList(String college) {
        if (StringUtils.isBlank(college)) {
            college = null;
        }
        List<NinCareer> ninCareerList = ninCareerMapper.getNinCareerList(college);
        return ninCareerList;
    }

    @Override
    public Map<String, List<NinCareer>> getCareerClassList() {
        List<NinCareer> ninCareers = ninCareerMapper.selectList(new QueryWrapper<>());
        Map<String, List<NinCareer>> collect = ninCareers.stream().filter(ninCareer -> !ninCareer.getCollege().equals("选修")).collect(Collectors.groupingBy(NinCareer::getCollege));
        return collect;
    }



    @Override
    public int addSingle(NinCareer ninCareer) {
        ninCareer.setId(IDUtil.getID());
        ninCareer.setCreateUserId(UserUtil.getUserInfo().getUserId());
        ninCareer.setModifyUserId(UserUtil.getUserInfo().getUserId());
        Integer integer = ninCareerMapper.selectCount(new QueryWrapper<>(new NinCareer() {{
            setCareerName(ninCareer.getCareerName());
        }}));
        if (integer == 0) {
            return ninCareerMapper.insert(ninCareer);
        } else {
            throw new ServiceException(412, "重名");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delById(Long id) {
        NinCareer ninCareer = ninCareerMapper.selectById(id);
        if (ninCareer.getClassNum() > 0) {
            throw new ServiceException(412, "该专业下存在班级");
        }

        //删除专业-课程表
        ninCareerCourseMapper.delete(new QueryWrapper<>(new NinCareerCourse() {{
            setCareerId(id);
        }}));
        return ninCareerMapper.deleteById(id);
    }

    @Override
    public int alterSingle(NinCareer ninCareer) {
        ninCareer.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninCareerMapper.updateById(ninCareer);
    }
}
