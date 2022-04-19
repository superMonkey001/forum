package cn.edu.hncj.forum.service.impl;

import cn.edu.hncj.forum.mapper.UserMapper;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    /**
     * 如果这个用户已经存在于数据库中，则执行创建，否则执行更新操作
     * @param user
     */
    @Override
    public void createOrUpdate(User user) {
        User dbUser = userMapper.findByAccountId(user.getAccountId());
        if(dbUser == null) {
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else {
            dbUser.setGmtModified(System.currentTimeMillis());
            dbUser.setToken(user.getToken());
            dbUser.setAvatarUrl(user.getAvatarUrl());
            dbUser.setName(user.getName());
            userMapper.update(dbUser);
        }
    }
}
