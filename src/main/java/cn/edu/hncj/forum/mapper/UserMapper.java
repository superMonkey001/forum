package cn.edu.hncj.forum.mapper;

import cn.edu.hncj.forum.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface UserMapper {
    @Insert("insert into user values (#{id},#{accountId},#{name},#{token},#{gmtCreate},#{gmtModified})")
    int insert(User user);
}
