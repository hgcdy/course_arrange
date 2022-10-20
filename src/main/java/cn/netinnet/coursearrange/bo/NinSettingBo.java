package cn.netinnet.coursearrange.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NinSettingBo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 开放的课程id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long courseId;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 开放状态序号 0-开放，1-不开放，2-定时
     */
    private Integer openState;

    private String state;

    /**
     * 开放时间
     */
    private String openTime;

    /**
     * 结束时间
     */
    private String closeTime;

}
