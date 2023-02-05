package cn.netinnet.coursearrange.enums;

public enum MsgEnum {
    HOUSE_APPLY(0, "教室申请: 教师[%s]，教室[%s]，班级[%s]，时间[%s]，用途[%s]"),
    CONSENT_APPLY(1, "您的教室申请已被管理员同意"),
    UNCONSENT_APPLY(2, "您的教室申请已被管理员退回"),
    ADD_COURSE(3, "管理员已为您添加课程[%s]"),
    DEL_COURSE(4, "您的课程[%s]已被管理员移除")
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

    public static MsgEnum codeOfKey(int code) {
        for (MsgEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
