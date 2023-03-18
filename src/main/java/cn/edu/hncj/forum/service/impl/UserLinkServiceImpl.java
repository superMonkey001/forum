package cn.edu.hncj.forum.service.impl;

import cn.edu.hncj.forum.mapper.UserLinkMapper;
import cn.edu.hncj.forum.service.UserLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author FanJian
 * @Date 2023-03-18 16:19
 */
@Service
public class UserLinkServiceImpl implements UserLinkService {
    @Autowired
    private UserLinkMapper userLinkMapper;

    public Long selectLinkId(Long fromId,Long toId){
        return userLinkMapper.selectLinkId(fromId,toId);
    }

    public void insertLink(Long min, Long max) {
        userLinkMapper.insertLink(min,max);
    }
}
