package cn.edu.hncj.forum.strategy;

import lombok.Data;

@Data
public class LoginUserInfo {
    private String name;
    private Long id;
    private String bio;
    private String avatar_url;
}
