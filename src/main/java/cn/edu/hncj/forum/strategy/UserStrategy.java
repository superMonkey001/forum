package cn.edu.hncj.forum.strategy;

public interface UserStrategy {
    LoginUserInfo getUser(String code, String state);

    String getSupportedType();
}
