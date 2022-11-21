package cn.netinnet.coursearrange.util;

public class IDUtil {

    public static long getID(){
        return SnowFlakeUtil.getSnowFlakeId();
    }

}
