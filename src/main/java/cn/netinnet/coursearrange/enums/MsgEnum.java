package cn.netinnet.coursearrange.enums;

public enum MsgEnum {
    ADD_COURSE(1, "管理员已为您添加课程[%s]"),
    DEL_COURSE(2, "您的课程[%s]已被管理员移除")
    ;


    private int code;
    private String msg;

    MsgEnum(int code, String msg) {
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

    public static String formatMsg(MsgEnum e, Object... obj){
        return String.format(e.getMsg(), obj);
    }
}
