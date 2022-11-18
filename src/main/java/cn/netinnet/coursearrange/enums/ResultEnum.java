package cn.netinnet.coursearrange.enums;

public enum ResultEnum {

    SUCCESS(200, "操作成功"),
    FAILURE(412, "操作失败"),

    CODE_NOT_EXIST(-1001, "账号不存在"),
    PW_ERROR(-1002, "密码错误"),

    EXIST(-2001, "【%s】已经存在"),
    NOT_EXIST(-2002, "【%s】不存在"),
    NAME_REPETITION(-2003, "【%s】已被使用"),
    COURSE_LIMIT(-2004, "【%s】所选课程已达上限"),
    NOT_NULL(-2005, "【%s】不为空"),
    NOT_SELECT(-2006, "没有【%s】选择【%s】"),


    ;

    private int code;
    private String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static ResultEnum formatMsg(ResultEnum e, Object... obj){
        String m = String.format(e.getMsg(), obj);
        e.setMsg(m);
        return e;
    }

}
