package cn.netinnet.coursearrange.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HouseBo {

    /**
     * 教室id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 教室名称
     */
    private String houseName;

    /**
     * 教室类型,0-教室，1-机房，2-实验室，3-课外，4-网课
     */
    private Integer houseType;
    private String cnHouseType;

    /**
     * 教室座位
     */
    private Integer seat;
}
