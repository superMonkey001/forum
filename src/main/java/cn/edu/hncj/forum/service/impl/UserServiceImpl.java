package cn.edu.hncj.forum.service.impl;

import cn.edu.hncj.forum.dto.ResultDTO;
import cn.edu.hncj.forum.exception.CustomizeErrorCode;
import cn.edu.hncj.forum.mapper.UserExtMapper;
import cn.edu.hncj.forum.mapper.UserMapper;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.model.UserExample;
import cn.edu.hncj.forum.service.UserService;
import cn.edu.hncj.forum.utils.JWTUtils;
import cn.edu.hncj.forum.utils.RandomLongUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserExtMapper userExtMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final String salt = UUID.randomUUID().toString().substring(0, 6);

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

    @Override
    public User findUserByName(String name) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andNameEqualTo(name);
        List<User> users = userMapper.selectByExample(userExample);

        return users.size() == 0 ? null : users.get(0);
    }

    /**
     * 在数据库中生成用户，以及返回token给前端
     * @param user 即将被注册的用户
     * @return 返回用户第一次注册时生成的token
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createUserAndReturnToken(User user) {
        try {
            user.setId(RandomLongUtils.randomLong(16));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String password = user.getPassword();
        password = DigestUtils.md5Hex(password + salt);
        user.setPassword(password);
        user.setSalt(salt);
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(System.currentTimeMillis());
        user.setBio("nothing");
        try {
            user.setAccountId(RandomLongUtils.randomLong(16) + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setType("custom");
        userMapper.insert(user);
        String token = JWTUtils.createToken(user.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(user),1, TimeUnit.DAYS);
        return token;
    }

    @Override
    public User checkToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        // 第一步校验
        Map<String, Object> map = JWTUtils.checkToken(token);
        if (map == null) {
            return null;
        }


        /// System.out.println("传入redis校验的token==========" + token);

        // 第二步redis校验
        String jsonUser = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(jsonUser)) {
            return null;
        }

        return JSON.parseObject(jsonUser, User.class);
    }

    @Override
    public String findEmail(String email) {
        String email1 = userExtMapper.findEmail(email);
        return email1;
    }


    @Override
    public void updatePassword(User user) {
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User selectByName(String name) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andNameEqualTo(name);
        List<User> users = userMapper.selectByExample(userExample);
        return users.size() == 0 ? null : users.get(0);
    }

    @Override
    public User findUserByNameAndPassword(String name,String password) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andNameEqualTo(name).andPasswordEqualTo(password);
        List<User> users = userMapper.selectByExample(userExample);
        return users.size() == 0 ? null : users.get(0);
    }


}
