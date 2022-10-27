package cn.netinnet.coursearrange.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CourseBo {

    /**
     * 课程id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程需要的教室类型.0-教室，1-机房，2-实验室，3-课外，4-网课
     */
    private Integer houseType;
    private String cnHouseType;

    /**
     * 是否必修，0-选修，1-必修
     */
    private Integer must;
    private String cnMust;

    /**
     * 课时
     */
    private Integer courseTime;

    /**
     * 最早开课时间
     */
    private Integer startTime;

    /**
     * 最晚结课时间
     */
    private Integer endTime;

    /**
     * 几周内结课
     */
    private Integer weekTime;

    /**
     * 最多上课班级
     */
    private Integer maxClassNum;
}
