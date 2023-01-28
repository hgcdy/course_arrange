package cn.netinnet.coursearrange.enums;

public enum CourseTypeEnum {
    OPTIONAL(0, "选修"),
    REQUIRED_COURSE(1, "必修")
    ;

    private int code;
    private String name;

    CourseTypeEnum(int code, String name) {
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

    public static CourseTypeEnum codeOfKey(int code) {
        for (CourseTypeEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
