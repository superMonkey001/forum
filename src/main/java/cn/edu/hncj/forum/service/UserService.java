package cn.edu.hncj.forum.service;

import cn.edu.hncj.forum.model.User;

public interface UserService {
    void createOrUpdate(User user);
    User findUserByName(String name);
    String createUserAndReturnToken(User user);
    void updatePassword(User user);
    User selectByName(String name);

    User findUserByNameAndPassword(String name,String password);
    User checkToken(String token);

    String findEmail(String email);
}
