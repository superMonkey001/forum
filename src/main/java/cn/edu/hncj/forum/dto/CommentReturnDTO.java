package cn.edu.hncj.forum.dto;

import cn.edu.hncj.forum.model.User;
import lombok.Data;

/**
 * @author FanJian
 */
@Data
public class CommentReturnDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Long likeCount;
    private String content;
    private Integer commentCount;
    private User user;
}