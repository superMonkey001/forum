package cn.edu.hncj.forum.mapper;

import cn.edu.hncj.forum.dto.QuestionQueryDTO;
import cn.edu.hncj.forum.model.Question;
import cn.edu.hncj.forum.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {

    /**
     * 通过问题id,增加阅读数
     * @param id
     */
    int incView(Question id);

    /**
     * 通过问题id,增加评论数
     */
    int incCommentCount(Question question);

    /**
     * 通过当前问题，找出相关问题
     * @param question 当前问题
     * @return 相关问题
     */
    List<Question> selectRelated(Question question);

    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}