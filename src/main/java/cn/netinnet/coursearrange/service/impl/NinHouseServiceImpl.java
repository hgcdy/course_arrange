package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.bo.HouseBo;
import cn.netinnet.coursearrange.entity.NinHouse;
import cn.netinnet.coursearrange.enums.HouseTypeEnum;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinHouseMapper;
import cn.netinnet.coursearrange.service.INinHouseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.istack.internal.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<HouseBo> list = ninHouseMapper.getSelectList(houseName, houseType, firstSeat, tailSeat);
        PageInfo<HouseBo> pageInfo = new PageInfo<>(list);

        pageInfo.getList().forEach(i -> {
            if (i.getHouseType() != null) {
                i.setCnHouseType(HouseTypeEnum.codeOfKey(i.getHouseType()).getName());
            }
        });

        HashMap<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return map;
    }

    @Override
    public int addSingle(NinHouse ninHouse) {
        int count = count(new LambdaQueryWrapper<NinHouse>().eq(NinHouse::getHouseName, ninHouse.getHouseName()));
        if (count > 0) {
            throw new ServiceException(412, "重名");
        }
        return ninHouseMapper.insert(ninHouse);
    }

    @Override
    public boolean delById(@NotNull Long id) {
        return removeById(id);
    }

    @Override
    public boolean alterSingle(NinHouse ninHouse) {
        int count = count(new LambdaQueryWrapper<NinHouse>()
                .eq(NinHouse::getHouseName, ninHouse.getHouseName())
                .ne(NinHouse::getId, ninHouse.getId()));
        if (count > 0) {
            throw new ServiceException(412, "重名");
        }
        return updateById(ninHouse);
    }

    @Override
    public NinHouse getHouseById(Long id) {
        return getById(id);
    }

    @Override
    public List<NinHouse> getHouseByType(Integer houseType) {
        List<NinHouse> ninHouses = ninHouseMapper.selectList(new LambdaQueryWrapper<NinHouse>()
                .select(NinHouse::getId, NinHouse::getHouseName)
                .eq(houseType != -1, NinHouse::getHouseType, houseType));
        return ninHouses;
    }
}
