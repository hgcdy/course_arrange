package cn.netinnet.coursearrange.enums;

public enum WeeklyTypeEnum {
    WEEKLY(0, "单双周"),
    ODD_WEEK(1, "单周"),
    DOUBLE_WEEK(2, "双周");


    private int code;
    private String name;

    WeeklyTypeEnum(int code, String name) {
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

    public static WeeklyTypeEnum codeOfKey(int code) {
        for (WeeklyTypeEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
