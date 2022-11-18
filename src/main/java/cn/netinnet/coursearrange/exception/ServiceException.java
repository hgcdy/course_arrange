package cn.netinnet.coursearrange.exception;

import cn.netinnet.coursearrange.enums.ResultEnum;

public class ServiceException extends RuntimeException {

    /**
     * 异常代码
     */
    private final int code;

    /**
     * 异常信息
     */
    private final String msg;

    public ServiceException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ServiceException(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public ServiceException(Exception e, String errorInfo) {
        this(500, errorInfo);
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return msg;
    }


}
