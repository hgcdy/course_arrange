package cn.netinnet.coursearrange.constant.impl;


import cn.netinnet.coursearrange.constant.IResultStatus;

/**
 * 功能描述: 返回状态码与错误描述
 *
 * @author limj
 * @date 2019/6/26 14:13
 */
public enum ResultStatus implements IResultStatus {
    /**
     * 建议错误码不要重复
     * <p>
     * 如果是动态通配符形式 请在替换部分写上{数字} 数字从0开始
     * 例如：用户{0}已经登陆，请{1}重新登陆   并且在抛异常时候把，对应的数组附上
     */
    SUCCESS(200, "成功"),
    FAIL(202, "失败"),
    UNAUTHORIZED(401, "未登录，请登录账号"),


    // 系统级错误代码 、通用模块00开头
    UPDATE_ERROR(10001, "更新不成功"),
    DELETE_ERROR(10002, "删除不成功"),
    INSERT_ERROR(10003, "新增不成功"),
    KAFKA_ERROR(10004, "消息服务异常"),//kafka...
    CALL_SUB_SYSTEM_ERROR(10005, "同步子系统异常"),


    //一般字段校验
    PARAMETER_IS_NOT_PRESENT(10003, "缺少请求参数"),
    PARAM_ERROR(10004, "参数错误"),
    SYSTEM_ERROR(10005, "系统错误"),
    DATABASE_ERROR(10006, "操作数据库出现异常：字段重复、有外键关联等"),
    AUTHENTICATE_ERROR(10007, "未经授权"),
    REQUEST_IS_ILLEGAL(10008, "非法请求"),
    COULD_NOT_READ_JSON(10009, "参数解析失败"),
    OBJECT_ID_NOT_NULL(10010, "Id不能为空"),
    JSON_UN_SYNTAX(10011, "JSON串不合法"),
    NOT_RECORD_OPT(10012, "请选择要操作的记录"),
    FILE_UPLOAD_FAIL(10013, "文件上传失败，请重新上传"),
    page_NOT_FOUND(10014, "页码或者条数没上传，分页是必传的"),
    CURRENT_USER_NOT_FOUND(10015, "CurrentUser注解错误！没有传递tokenUserId参数或没有找到该用户"),

    // 用户信息01开头
    USERNAME_OR_PASSWORD_ERROR(10101, "用户名或密码错误"),
    USER_NOT_LOGIN(10102, "用户未登录"),
    USER_NOT_FOUND(10103, "用户不存在"),
    USER_ACCOUNT_EXIST(10104, "账号已存在"),
    USER_PWD_ERROR(10105, "原密码错误"),
    USER_AUTH_ERROR(10106, "用户认证失败"),
    USER_ACCOUNT_FORBIDDEN(10107, "账号已被禁用"),

    USER_NOT_ACCESS(10108, "用户未开通权限"),


    // 字典信息02开头
    CODE_TYPE_NOT_FOUND(10201, "字典类型不存在"),
    CODE_TYPE_EXIST(10202, "字典类型已存在"),
    CODE_NOT_FOUND(10203, "字典不存在"),
    CODE_EXIST(10204, "字典已存在"),
    CODE_TYPE_NOT_SEL(10205, "没有选择要删除的字典类别"),
    CODE_NOT_SEL(10206, "没有选择要删除的字典明细"),

    // 组织机构03开头
    USER_ORG_ISEXIST(10301, "组织机构已存在"),

    // 用户角色04开头
    USER_ROLE_ISEXIST(10401, "该编码已存在"),

    // 功能权限 05 开头
    MENU_FUN_CODE_EXIST(10502, "权限编号已存在"),
    MENU_FUN_URL_EXIST(10503, "权限URL已存在"),
    MENU_FUN_NAME_EXIST(10504, "同一个菜单下权限名称已存在"),


    // 参数信息06开头
    PARA_NOT_FOUND(10601, "参数信息不存在"),
    PARA_EXIST(10602, "参数信息已存在"),

    // 菜单信息07开头
    MENU_HAVE_FUN(10701, "此菜单存在功能权限绑定，不可删除"),
    MENU_NAME_EXIST(10702, "菜单名称已存在"),
    MENU_FATHER_NOT_EXIST(10703, "父菜单未保存"),
    MENU_HAVE_USER_ROLE(10704, "此菜单存在角色权限绑定，不可删除"),

    // 幻灯片信息08开头
    WEB_RECOMMEND_EXIST(10801, "幻灯片已存在"),
    WEB_RECOMMEND_DELETE_NOT_SELECTED(10802, "没有选择要删除的记录"),

    // 应用相关信息09开头
    APP_TASK_EXIST(10901, "任务已存在"),
    APP_TASK_DELETE_NOT_SELECTED(10902, "没有选择要删除的记录"),
    APP_EXIST(10903, "应用已存在"),
    APP_DELETE_NOT_SELECTED(10904, "没有选择要删除的记录"),
    APP_VERSION_EXIST(10905, "应用版本已经存在"),
    APP_VERSION_NOT_FOUND(10906, "项目版本不存在"),
    APP_VERSION_NOT_SEL(10907, "没有选择要删除的项目版本"),
    APP_PRACTICE_EXIST(10908, "实训应用已存在"),
    APP_PRACTICE_DELETE_NOT_SELECTED(10909, "没有选择要删除的记录"),
    APP_PACKAGE_EXIST(10910, "应用套餐已存在"),
    APP_PACKAGE_DELETE_NOT_SELECTED(10911, "没有选择要删除的记录"),
    APP_PRACTICE_START(10912, "实训已经开始，不能修改"),
    APP_PRACTICE_STUDENT_EMPTY(10913, "当前实训没有添加学生，请添加后再试"),

    // 课程相关 10开头
    COURSE_LOOK_SET_ERROR(11001, "试看设置为空"),
    COURSE_RECOMMEND_EXIST(11002, "该课程已经推广，请进行修改操作"),
    COURSE_LIST_CONNECT_FAIL(11003, "查找课程列表失败"),
    COURSE_FROM_TYPE_CONNECT_FAIL(11004, "获取课程来源类型失败"),
    COURSE_ON_STATUS_CONNECT_FAIL(11005, "获取课程上下架状态失败"),
    COURSE_CONTENT_CONNECT_FAIL(11006, "获取课程章节信息失败"),
    COURSE_INFO_CONNECT_FAIL(11007, "获取课程基础信息失败"),
    COURSE_TEAM_CONNECT_FAIL(11008, "获取课程教师团队失败"),
    COURSE_COUNT_CONNECT_FAIL(11009, "获取课程统计信息失败"),
    COURSE_ID_NOT_NULL(11010, "课程ID不能为空"),
    COURSE_ORG_CODE_NOT_NULL(11010, "所属机构编码不能为空"),
    COURSE_EXIST_USED(11011, "【{0}】已经被使用，不可删除"),
    COURSE_RESOURCE_CONNECT_FAIL(11012, "课程资源获取失败"),
    COURSE_MSG_CONNECT_FAIL(11013, "{0}"),
    COURSE_BILL_WEIGHT_NOT_FOUND(11014, "该表单未设置表单权重"),

    // 高校用户信息01开头
    SCHOOL_USER_EXIST(11101, "该账号已存在"),
    SCHOOL_AUTH_FAIL(11102, "该账号已存在，认证失败"),
    SCHOOL_DELETE_FAIL(11103, "该账号已认证，删除失败"),
    SCHOOL_DELETE_BATCH_FAIL(11104, "存在已认证的用户，删除失败"),
    SCHOOL_AUTH_BATCH_FAIL(11105, "{0}认证失败，请手动认证"),
    SCHOOL_USER_INFO_EXIST(11106, "第{0}行，{1}已存在"),
    SCHOOL_USER_INFO_NULL(11107, "第{0}行，{1}为空"),
    SCHOOL_USER_INFO_WRONG(11108, "档案信息有误，请确认姓名与学号信息是否正确"),
    SCHOOL_AUTH_EXIST_FAIL(11109, "该账号已被认证，请重新选择"),
    SCHOOL_AUTH_OVER_LIMIT(11110, "认证失败，认证人数已满"),
    SCHOOL_USER_OVER_LIMIT(11111, "添加失败，学生人数已满"),
    SCHOOL_IMPORT_USER_OVER_LIMIT(11112, "批量添加失败，学生数限制{0}人，已存在{1}人"),
//    SCHOOL_USER_AUTH_OVER_LIMIT(11111, "{0}认证失败，认证人数已满"),

    // 组织部门相关 12开头
    USER_ORG_DEPART_EXIST(11201, "该部门名称在该组织的对应层级下已存在"),
    USER_ORG_DEPART_DELETE_FAIL(11202, "该部门组织下存在用户，不允许删除，请先清除该部门组织下的用户再做删除"),
    USER_ORG_DEPART_CHILDREN_DELETE_FAIL(11203, "该部门组织有子部门组织，不允许删除，请先清除"),

    // 网站前台 教学班课程相关 13开头
    TEACHING_CLASS_CONNECT_FAIL(11301, "查找教学课程失败"),
    TEACHING_CLASS_ADD_FAIL(11302, "学生已加入教学课程"),
    //教学班参加学习
    TEACHING_CLASS_LEARNING_NOT_LOGIN(11303, "未登录"),
    TEACHING_CLASS_LEARNING_NOT_JOIN_SCHOOL(11304, "已登录，未加入学校"),
    TEACHING_CLASS_LEARNING_NEED_PWD(11305, "已登录，已加入学校，需要密码"),
    TEACHING_CLASS_LEARNING_NOT_NEED_PWD(11306, "已登录，已加入学校，不需要密码"),
    TEACHING_CLASS_GETISQUOTE_FAIL(11307, "获取课程是否引用失败"),

    //授权课程或应用 14开头
    DELETE_ORG_AUTH_CONF_WARN(11401, "被引用的授权资源无法删除"),

    // 网站前台 实训班课程相关 15开头
    SX_CLASS_CONNECT_FAIL(11501, "查找实训班课程失败"),
    SX_CLASS_ADD_FAIL(11502, "学生加入实训班课程失败"),
    SX_CLASS_ADD_FAIL_EXISTED(11505, "学生已加入实训班课程"),

    // 直播相关 16开头
    LIVE_ROOM_CONNECT_FAIL(11601, "查找直播间列表失败"),
    LIVE_ROOM_CODE_FAIL(11601, "查找直播间代码失败"),

    // 考试相关 17 开头
    EXAM_NAME_EXIST(11701, "考试名称已经存在"),
    EXAM_INCLUDE_SESSION(11702, "考试中包含场次,请先删除场次"),

    // 场次相关 18 开头
    SESSION_NAME_EXIST(11801, "场次名称已经存在"),
    SESSION_IS_OVER(11802, "场次已经结束"),

    // 试卷相关 19 开头
    PAPER_EVALUATE_MOULD_COINSIDE(11901, "模板成绩区域重合"),
    PAPER_EVALUATE_MOULD_NO_EXIST(11902, "模板成绩不存在"),
    PAPER_INCLUDE_SESSION(11903, "已有场次引用该试卷, 请先删除场次"),
    PAPER_USED_SESSION(11904, "已有场次引用该试卷, 无法修改状态"),
    PAPER_EXIT(11905, "已存在相同的试卷名称"),

    // 补时相关20开头
    EXTRA_TIME_EXIST(12001, "补时记录已存在，无法继续添加"),
    EXTRA_TIME_RUNNING(12002, "学生正在补时无法撤销"),
    EXTRA_TIME_NOT_EXIST(12003, "已无补时记录"),

    //手机绑定信息相关
    PHONE_PIC_ERROR(13101, "图形验证码错误"),
    PHONE_SEND_FREQUENTLY(13102, "发送短信过于频繁"),
    PHONE_NUM_ERROR(13103, "号码格式有误或该号码今日短信发送已达上限"),
    PHONE_CODE_SUCCESS(13104, "获取验证码成功，请及时查看您的手机验证码"),
    PHONE_CODE_EXPIRED(13105, "短信验证码已过期，请重新获取"),
    PHONE_CODE_WRONG(13106, "短信验证码错误，请重新输入"),
    PHONE_NUM_EXIST(13107, "手机号码已经注册"),

    SESSION_ID_ERROR(14101, "已被禁用,查看异常");
    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回结果描述
     */
    private String msg;

    ResultStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
