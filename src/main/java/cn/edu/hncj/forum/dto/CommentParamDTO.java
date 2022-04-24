package cn.edu.hncj.forum.dto;

import lombok.Data;

/**
 * @author FanJian
 */
@Data
public class CommentParamDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
