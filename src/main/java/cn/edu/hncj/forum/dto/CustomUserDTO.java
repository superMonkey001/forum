package cn.edu.hncj.forum.dto;

import lombok.Data;

/**
 * 自定义用户类
 * @Author FanJian
 * @Date 2022/5/13
 */
@Data
public class CustomUserDTO {
    private String name;
    private String password;
    private String email;
    private Long id;
    private String bio;
    private String avatar_url;
}
