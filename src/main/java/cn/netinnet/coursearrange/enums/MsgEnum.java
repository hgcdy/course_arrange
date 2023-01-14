package cn.netinnet.coursearrange.enums;

public enum MsgEnum {
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

    public static MsgEnum formatMsg(MsgEnum e, Object... obj){
        String m = String.format(e.getMsg(), obj);
        e.setMsg(m);
        return e;
    }
}
