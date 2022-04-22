package cn.edu.hncj.forum.enums;


public enum CommentTypeEnum {
    QUESTION(1),
    COMMENT(2);
    private Integer type;
    CommentTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
