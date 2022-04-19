package cn.edu.hncj.forum.mapper;

import cn.edu.hncj.forum.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Insert("insert into question (title,description,gmt_create,gmt_modified,tag,creator) values" +
            " (#{title},#{description},#{gmtCreate},#{gmtModified},#{tag},#{creator})")
    void  create(Question question);

    @Select("select * from question limit #{offset},#{size}")
    List<Question> list(@Param("offset") Integer offset, @Param("size") Integer size);

    @Select("select count(*) from question")
    Integer count();

    /**
     * 通过用户id查找他创建的所有问题
     * @param creator
     * @param offset
     * @param size
     * @return
     */
    @Select("select * from question where creator = #{creator} limit #{offset},#{size}")
    List<Question> listByCreator(@Param("creator") Integer creator,@Param("offset") Integer offset,@Param("size") Integer size);


    /**
     * 查询creator创建的问题数
     * @param creator
     * @return
     */
    @Select("select count(*) from question where creator = #{creator}")
    Integer countByCreator(Integer creator);

    @Select("select * from question where id = #{id}")
    Question findById(Integer id);
}
