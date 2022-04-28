package cn.edu.hncj.forum.service.impl;

import cn.edu.hncj.forum.mapper.UserMapper;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.model.UserExample;
import cn.edu.hncj.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 如果这个用户已经存在于数据库中，则执行创建，否则执行更新操作
     *
     * @param user 前端传入后端的用户数据
     */
    @Override
    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(user.getAccountId()).andTypeEqualTo(user.getType());
        List<User> users = userMapper.selectByExample(userExample);
        User dbUser = null;
        if (users != null && users.size() != 0) {
            dbUser = users.get(0);
        }
        if (dbUser == null) {
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        } else {
            User updateUser = new User();
            updateUser.setId(dbUser.getId());
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setToken(user.getToken());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            userMapper.updateByPrimaryKeySelective(updateUser);
        }
    }
}
