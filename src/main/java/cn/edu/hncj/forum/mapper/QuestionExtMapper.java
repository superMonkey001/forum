package cn.edu.hncj.forum.mapper;

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
    void incView(Integer id);
}