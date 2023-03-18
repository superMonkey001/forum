package cn.edu.hncj.forum.service;

/**
 * @Author FanJian
 * @Date 2023-03-18 16:19
 */

public interface UserLinkService {

    Long selectLinkId(Long fromId,Long toId);
    void insertLink(Long min, Long max);
}
