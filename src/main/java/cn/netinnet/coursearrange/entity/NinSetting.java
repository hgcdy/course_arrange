package cn.netinnet.coursearrange.entity;

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
public class NinSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设置记录id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 开放的课程id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("course_id")
    private Long courseId;

    /**
     * 用户类型
     */
    @TableField("user_type")
    private String userType;

    /**
     * 开放状态 0-开放，1-不开放，2-定时
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

    /**
     * 逻辑删除标识
     */
    @TableField("del_flag")
    private Integer delFlag;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 创建人id
     */
    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 更新时间
     */
    @TableField("modify_time")
    private LocalDateTime modifyTime;

    /**
     * 修改人id
     */
    @TableField("modify_user_id")
    private Long modifyUserId;


}
