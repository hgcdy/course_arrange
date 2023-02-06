package cn.netinnet.coursearrange.enums;


public enum HouseTypeEnum {
    CLASS_ROOM(0, "教室"),
    MACHINE_ROOM(1, "机房"),
    LABORATORY(2, "实验室")
    ;

    private int code;
    private String name;

    HouseTypeEnum(int code, String name) {
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

    public static HouseTypeEnum codeOfKey(int code) {
        for (HouseTypeEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
