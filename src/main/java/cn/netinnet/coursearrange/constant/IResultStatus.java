package cn.netinnet.coursearrange.constant;

/**
 * 自定义ajax返回状态枚举类的抽象接口
 * {
 * code: Integer,
 * message: String
 * }
 *
 * @author zhenglm
 */
public interface IResultStatus {

    /**
     * 返回code
     *
     * @return code
     */
    Integer getCode();

    /**
     * 设置code
     *
     * @param code code
     */
    void setCode(Integer code);

    /**
     * 返回message
     *
     * @return message
     */
    String getMsg();

    /**
     * 设置message
     *
     * @param msg message
     */
    void setMsg(String msg);

    /**
     * 根据code返回ajax返回状态枚举对象
     *
     * @param aClass 枚举类
     * @param code   code
     * @return IResultStatus
     */
    static <T extends IResultStatus> T valueOf(Class<T> aClass, Integer code) {
        T[] array = aClass.getEnumConstants();
        T retVal = null;
        for (T e : array) {
            if (e.getCode().equals(code)) {
                retVal = e;
                break;
            }
        }
        return retVal;
    }
}
