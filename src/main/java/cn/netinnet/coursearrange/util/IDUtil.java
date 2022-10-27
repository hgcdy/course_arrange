package cn.netinnet.coursearrange.util;

public class IDUtil {

    public IDUtil() {
    }

    public static long getID(){
        return SnowFlakeUtil.getSnowFlakeId();
    }

}
