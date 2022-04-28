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
    COMMENT_NOT_FOUND(2006, "您评论的问题不见啦"),
    CONTENT_IS_EMPTY(2007, "回复内容不能为空"),
    READ_NOTIFICATION_FAIL(2008, "你读的是别人的回复！！！"),
    NOTIFICATION_NOT_FOUND(2009, "消息不翼而飞啦~~~");

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
