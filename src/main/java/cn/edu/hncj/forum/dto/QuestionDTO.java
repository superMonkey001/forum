package cn.edu.hncj.forum.dto;

import cn.edu.hncj.forum.model.User;
import lombok.Data;

/**
 * DTO数据传输对象，和数据库对象作区分。
 */
@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String description;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer viewCount;
    private Integer likeCount;
    private Long creator;
    private Integer commentCount;
    private String tag;
    private User user;
}
