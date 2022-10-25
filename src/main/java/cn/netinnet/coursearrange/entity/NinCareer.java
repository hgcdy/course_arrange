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
 * @since 2022-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("nin_career")
public class NinCareer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 专业id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 学院
     */
    @TableField("college")
    private String college;

    /**
     * 专业名称
     */
    @TableField("career_name")
    private String careerName;

    /**
     * 班级数量
     */
    @TableField("class_num")
    private Integer classNum;

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
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("modify_user_id")
    private Long modifyUserId;


}
