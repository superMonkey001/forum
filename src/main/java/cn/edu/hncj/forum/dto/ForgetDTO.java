package cn.edu.hncj.forum.dto;

import lombok.Data;

/**
 * @Author FanJian
 * @Date 2022/5/15
 */
@Data
public class ForgetDTO {
    String name;
    String email;
    String checkCode;
    String password;
}
