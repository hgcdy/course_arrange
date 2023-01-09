package cn.netinnet.coursearrange.bo;

import cn.netinnet.coursearrange.entity.NinSetting;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SettingBo extends NinSetting {

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 开放状态（中文）
     */
    private String state;
}
