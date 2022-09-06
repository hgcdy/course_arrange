package cn.netinnet.coursearrange.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * <p>
 * 
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("nin_arrange")
public class NinArrange implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("class_id")
    private Long classId;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("teacher_id")
    private Long teacherId;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("course_id")
    private Long courseId;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("house_id")
    private Long houseId;

    /**
     * 是否选修，0-选修，1-必修
     */
    private Integer must;

    /**
     * 周次
     */
    private Integer weekly;

    /**
     * 星期
     */
    private Integer week;

    /**
     * 节数
     */
    @TableField("pitch_num")
    private Integer pitchNum;

    @TableField("del_flag")
    private Integer delFlag;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 更新时间
     */
    @TableField("modify_time")
    private LocalDateTime modifyTime;

    @TableField("modify_user_id")
    private Long modifyUserId;


}
