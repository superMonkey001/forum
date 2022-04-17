package cn.edu.hncj.forum.mapper;

import cn.edu.hncj.forum.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper {
    @Insert("insert into question (title,description,gmt_create,gmt_modified,tag,creator) values" +
            " (#{title},#{description},#{gmtCreate},#{gmtModified},#{tag},#{creator})")
    void  create(Question question);
}
