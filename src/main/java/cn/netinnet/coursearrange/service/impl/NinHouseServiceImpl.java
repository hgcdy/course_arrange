package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinHouse;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinHouseMapper;
import cn.netinnet.coursearrange.service.INinHouseService;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import cn.netinnet.coursearrange.util.Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.istack.internal.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@Service
public class NinHouseServiceImpl extends ServiceImpl<NinHouseMapper, NinHouse> implements INinHouseService {

    @Autowired
    private NinHouseMapper ninHouseMapper;

    @Override
    public Map<String, Object> getPageSelectList(Integer page, Integer size,  String houseName, Integer houseType, Integer firstSeat, Integer tailSeat) {
        PageHelper.startPage(page, size);
        List<Map<String, Object>> list = ninHouseMapper.getSelectList(houseName, houseType, firstSeat, tailSeat);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        Utils.conversion(pageInfo.getList());
        HashMap<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return map;
    }

    @Override
    public int addSingle(NinHouse ninHouse) {
        Integer integer = ninHouseMapper.selectCount(
                new QueryWrapper<NinHouse>()
                        .eq("house_name", ninHouse.getHouseName()));
        if (integer > 0) {
            throw new ServiceException(412, "重名");
        }
        ninHouse.setId(IDUtil.getID());
        ninHouse.setCreateUserId(UserUtil.getUserInfo().getUserId());
        ninHouse.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninHouseMapper.insert(ninHouse);
    }

    @Override
    public int delById(@NotNull Long id) {
        return ninHouseMapper.deleteById(id);
    }

    @Override
    public int alterSingle(NinHouse ninHouse) {
        Integer integer = ninHouseMapper.selectCount(
                new QueryWrapper<NinHouse>()
                        .eq("house_name", ninHouse.getHouseName())
                        .ne("id", ninHouse.getId()));
        if (integer > 0) {
            throw new ServiceException(412, "重名");
        }
        ninHouse.setModifyUserId(UserUtil.getUserInfo().getUserId());
        return ninHouseMapper.updateById(ninHouse);
    }

    @Override
    public NinHouse getHouseById(Long id) {
        return ninHouseMapper.selectById(id);
    }
}
