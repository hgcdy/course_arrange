package cn.netinnet.coursearrange.enums;

public enum OpenStateEnum {
    UNOPEN(0, "未开放"),
    OPEN(1, "开放中"),
    FINISHED(2, "已结束");

    private int code;
    private String name;

    OpenStateEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static OpenStateEnum codeOfKey(int code) {
        for (OpenStateEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
