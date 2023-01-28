package cn.netinnet.coursearrange.init;

import cn.netinnet.coursearrange.entity.NinSetting;
import cn.netinnet.coursearrange.enums.OpenStateEnum;
import cn.netinnet.coursearrange.service.INinSettingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
public class SettingInit implements CommandLineRunner {

    @Autowired
    private INinSettingService ninSettingService;

    @Override
    public void run(String... args) throws Exception {
        List<NinSetting> ninSettings = ninSettingService.list(new LambdaQueryWrapper<NinSetting>()
                .in(NinSetting::getOpenState, new ArrayList<Integer>(){{
                    add(OpenStateEnum.UNOPEN.getCode());
                    add(OpenStateEnum.OPEN.getCode());
                }}));

        List<NinSetting> updateSettingList = new ArrayList<>();
        //避免项目关闭中时，时间过期
        ninSettings.forEach(setting -> {
            Integer openState = setting.getOpenState();
            LocalDateTime openTime = setting.getOpenTime();
            LocalDateTime closeTime = setting.getCloseTime();
            LocalDateTime nowTime = LocalDateTime.now();
            if (null != openTime && null != closeTime) {
                if (openTime.isAfter(nowTime) && openState != OpenStateEnum.UNOPEN.getCode()) {
                    setting.setOpenState(OpenStateEnum.UNOPEN.getCode());
                    updateSettingList.add(setting);
                } else if (nowTime.isAfter(closeTime) && openState != OpenStateEnum.FINISHED.getCode()) {
                    setting.setOpenState(OpenStateEnum.FINISHED.getCode());
                    updateSettingList.add(setting);
                } else if (nowTime.isAfter(openTime) && closeTime.isAfter(nowTime) && openState != OpenStateEnum.OPEN.getCode()){
                    setting.setOpenState(OpenStateEnum.OPEN.getCode());
                    updateSettingList.add(setting);
                }
            }
        });
        if (!updateSettingList.isEmpty()) {
            ninSettingService.updateBatchById(updateSettingList);
        }
        ninSettingService.addTimer(ninSettings);
    }
}
