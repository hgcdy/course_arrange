package cn.netinnet.coursearrange.entity;

import cn.netinnet.coursearrange.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

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
public class NinHouse extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


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

}
