package cn.netinnet.coursearrange.util;

public class IDUtil {
    public static long getID(){
        StringBuilder str = new StringBuilder("");
        for (int i = 0; i < 18; i++) {
            int s = (int) (Math.random() * 10);
            str.append(s);
        }
        long l = Long.parseLong(str.toString(), 10);
        return l;
    }
}
