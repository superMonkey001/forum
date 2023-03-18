package cn.edu.hncj.forum.model;

import lombok.Data;

/**
 * @Author FanJian
 * @Date 2023-03-18 16:17
 */

@Data
public class UserLink {
    private int linkId;
    private Long fromId;
    private Long toId;
}
