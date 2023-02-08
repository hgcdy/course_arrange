package cn.netinnet.coursearrange.bo;

import cn.netinnet.coursearrange.entity.NinSetting;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
public class SettingBo extends NinSetting {

    /**
     * 开放状态（中文）
     */
    private String state;

    private Long openTimestamp;
    private Long closeTimestamp;
}
