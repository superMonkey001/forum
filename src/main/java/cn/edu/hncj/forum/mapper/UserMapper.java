package cn.edu.hncj.forum.mapper;

import cn.edu.hncj.forum.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("insert into user (id,account_id,name,token,gmt_create,gmt_modified) values (#{id},#{accountId},#{name},#{token},#{gmtCreate},#{gmtModified})")
    int insert(User user);

    /**
     * 通过自定义的token变量来查询数据库对应的user
     * @param token
     * @return
     */
    @Select("select * from user where token = #{token}")
    User findUserByToken(@Param("token") String token);
}
