package cn.netinnet.coursearrange.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AvailableBo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long teacherId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long houseId;

    private String teacherName;

    private String houseName;

    private Integer week;

    private Integer pitchNum;

}
