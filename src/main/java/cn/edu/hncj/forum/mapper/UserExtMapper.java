package cn.edu.hncj.forum.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @Author FanJian
 * @Date 2022/5/17
 */
@Mapper
public interface UserExtMapper {
    String findEmail(String email);
}
