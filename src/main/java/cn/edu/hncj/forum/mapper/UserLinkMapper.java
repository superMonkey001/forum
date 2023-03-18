package cn.edu.hncj.forum.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Author FanJian
 * @Date 2023-03-18 16:18
 */

public interface UserLinkMapper {
    Long selectLinkId(@Param("fromId") Long fromId, @Param("toId") Long toId);

    void insertLink(@Param("min") Long min,@Param("max") Long max);
}
