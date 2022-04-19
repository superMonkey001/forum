package cn.edu.hncj.forum.mapper;

import cn.edu.hncj.forum.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {
    @Insert("insert into user (id,account_id,name,token,gmt_create,gmt_modified,avatar_url) values (#{id},#{accountId},#{name},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    int insert(User user);

    /**
     * 通过自定义的token变量来查询数据库对应的user
     * @param token
     * @return
     */
    @Select("select * from user where token = #{token}")
    User findUserByToken(@Param("token") String token);

    @Select("select * from user where id = #{id}")
    User findById(Integer id);

    @Select("select * from user where account_id = #{accountId}")
    User findByAccountId(String accountId);

    @Update("update user set token = #{token},avatar_url = #{avatarUrl},name = #{name},gmt_modified = #{gmtModified} where id = #{id}")
    void update(User dbUser);
}
