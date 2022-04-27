package cn.edu.hncj.forum.dto;

import lombok.Data;

/**
 * @author FanJian
 */
@Data
public class QuestionQueryDTO {
    private String search;
    private Integer offset;
    private Integer size;
}
