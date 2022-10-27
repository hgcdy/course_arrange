package cn.netinnet.coursearrange.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NinClassBo {

    /**
     * 学院
     */
    private String college;

    /**
     * 专业id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long careerId;

    /**
     * 专业名称
     */
    private String careerName;

    /**
     * 班级id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long classId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 课程数量
     */
    private Integer courseNum;

    /**
     * 班级人数
     */
    private Integer peopleNum;
}
