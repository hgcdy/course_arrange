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
 * @since 2022-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("nin_house")
public class NinHouse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 教室id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 教室名称
     */
    @TableField("house_name")
    private String houseName;

    /**
     * 教室类型,0-教室，1-机房，2-实验室，3-课外，4-网课
     */
    @TableField("house_type")
    private Integer houseType;

    /**
     * 教室座位
     */
    @TableField("seat")
    private Integer seat;

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
