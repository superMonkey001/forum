package cn.edu.hncj.forum.strategy;

import cn.edu.hncj.forum.dto.AccessTokenDTO;
import cn.edu.hncj.forum.provider.GithubProvider;
import cn.edu.hncj.forum.provider.dto.GithubUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GithubUserStrategy implements UserStrategy {
    @Autowired
    private GithubProvider githubProvider;

    @Override
    public LoginUserInfo getUser(String code, String state) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getGithubUser(accessToken);
        LoginUserInfo loginUserInfo = new LoginUserInfo();
        BeanUtils.copyProperties(githubUser, loginUserInfo);
        return loginUserInfo;
    }

    @Override
    public String getSupportedType() {
        return "github";
    }
}
