package cn.netinnet.coursearrange.enums;

public enum UserTypeEnum {
    ADMIN(0, "admin"),
    STUDENT(1, "student"),
    TEACHER(2, "teacher"),

    CLAZZ(-1, "class");

    private int code;
    private String name;

    UserTypeEnum(int code, String name) {
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

    public static UserTypeEnum codeOfKey(int code) {
        for (UserTypeEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
