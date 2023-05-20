package cn.edu.hncj.forum.exception;


/**
 * @author FanJian
 */

public enum CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND(2001, "你找到问题不存在"),
    TARGET_PARAM_NOT_FOUND(2002, "未选中任何问题或评论进行回复"),
    NO_LOGIN(2003, "当前操作需要登录，请登录后重试"),
    SYS_ERROR(2004, "服务器冒烟了~~~"),
    TYPE_PARAM_ERROR(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "您评论的回复不见啦"),
    CONTENT_IS_EMPTY(2007, "回复内容不能为空"),
    READ_NOTIFICATION_FAIL(2008, "你读的是别人的回复！！！"),
    NOTIFICATION_NOT_FOUND(2009, "消息不翼而飞啦~~~"),
    FILE_UPLOAD_FAIL(2010,"图片上传失败"),
    LINK_DOES_NOT_EXIST(2011,"这个链接不存在!!"),
    REGISTER_PARAM_NOT_NULL(2012,"参数不能为空"),
    USER_IS_EXIST(2013,"用户已存在，请更换用户名"),
    FORGET_PASSWORD_PARAM_NOT_NULL(2014,"请输入完全"),
    USER_NOT_FOUND(2014,"查询不到当前用户"),
    USER_OR_PASSWORD_ERROR(2015,"用户名或密码错误"),
    USER_OR_PASSWORD_IS_NULL(2016,"用户名或密码为空"),
    TOKEN_ERROR(2017,"token不合法"),
    EMAIL_IS_USED(2018,"该邮箱已被注册"),
    NOT_ALLOW_TALK_YOURSELF(2019,"不能跟自己聊天"),
    REC_USER_NOT_EXIST(2020,"聊天的用户不存在"),
    CLICK_TOO_FAST(2021,"手速太快啦")
    ;

    private String message;
    private Integer code;

    CustomizeErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
