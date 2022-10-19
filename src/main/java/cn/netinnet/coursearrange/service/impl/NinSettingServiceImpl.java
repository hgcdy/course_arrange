package cn.netinnet.coursearrange.service.impl;

import cn.netinnet.coursearrange.entity.NinSetting;
import cn.netinnet.coursearrange.exception.ServiceException;
import cn.netinnet.coursearrange.mapper.NinSettingMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinSettingService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangjs
 * @since 2022-10-19
 */
@Service
public class NinSettingServiceImpl extends ServiceImpl<NinSettingMapper, NinSetting> implements INinSettingService {

    @Autowired
    private NinSettingMapper ninSettingMapper;


    @Override
    public List<Map<String, Object>> getSelectList(String userType, Integer openState, String courseName) {
        List<Map<String, Object>> list = ninSettingMapper.getSelectList(userType, openState, courseName);
        List<Map<String, Object>> result = list.stream().map(i -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String openTime = dtf.format((LocalDateTime) i.get("openTime"));
            i.put("openTime", openTime);
            String closeTime = dtf.format((LocalDateTime) i.get("closeTime"));
            i.put("closeTime", closeTime);
            return i;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    public ResultModel alterBatch(String settingIds, Integer openState, Date openTime, Date closeTime) {
        List<Long> settingIdList = JSON.parseArray(settingIds, Long.class);
        if (settingIdList != null && settingIdList.size() != 0) {
            ninSettingMapper.alterBatch(settingIdList, openState, openTime, closeTime);
            return ResultModel.ok();
        } else {
            throw new ServiceException(412, "id不得为空");
        }
    }


}
