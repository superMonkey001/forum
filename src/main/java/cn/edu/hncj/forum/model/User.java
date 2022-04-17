package cn.edu.hncj.forum.model;

import lombok.Data;

/**
 * 对应数据库的user表
 */
@Data
public class User {
    //以下为数据库中的字段
    private Integer id;
    private String accountId;
    private String name;
    private String token;
    private Long gmtCreate;
    private Long gmtModified;
    private String avatarUrl;
}
