package cn.edu.hncj.forum.model;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class Question {
    //id  title   description  gmt_create  gmt_modified  view_count  like_count  tag
    private Integer id;
    private String title;
    private String description;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer viewCount;
    private Integer likeCount;
    private Integer creator;
    private Integer commentCount;
    private String tag;
}