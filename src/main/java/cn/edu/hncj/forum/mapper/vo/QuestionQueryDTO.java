package cn.edu.hncj.forum.mapper.vo;

import lombok.Data;

/**
 * @author FanJian
 */
@Data
public class QuestionQueryDTO {
    private String search;
    private Integer offset;
    private Integer size;
    private String hotTag;
}
