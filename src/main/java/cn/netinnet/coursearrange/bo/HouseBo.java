package cn.netinnet.coursearrange.bo;

import cn.netinnet.coursearrange.entity.NinHouse;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HouseBo extends NinHouse {

    private String cnHouseType;

}
