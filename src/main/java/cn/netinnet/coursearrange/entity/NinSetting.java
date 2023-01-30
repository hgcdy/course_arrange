package cn.netinnet.coursearrange.entity;

import cn.netinnet.coursearrange.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangjs
 * @since 2022-10-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("nin_setting")
public class NinSetting extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 开放的课程id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("course_id")
    private Long courseId;

    /**
     * 课程名称
     */
    @TableField("course_name")
    private String courseName;

    /**
     * 用户类型
     */
    @TableField("user_type")
    private String userType;

    /**
     * 开放状态 0-未开放，1-开放中，2-已结束
     */
    @TableField("open_state")
    private Integer openState;

    /**
     * 开放时间
     */
    @TableField("open_time")
    private LocalDateTime openTime;

    /**
     * 结束时间
     */
    @TableField("close_time")
    private LocalDateTime closeTime;
}
