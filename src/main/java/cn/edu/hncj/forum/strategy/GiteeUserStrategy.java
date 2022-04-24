package cn.edu.hncj.forum.strategy;

import cn.edu.hncj.forum.dto.AccessTokenDTO;
import cn.edu.hncj.forum.provider.GiteeProvider;
import cn.edu.hncj.forum.provider.dto.GiteeUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GiteeUserStrategy implements UserStrategy{
    @Autowired
    private GiteeProvider giteeProvider;

    @Override
    public LoginUserInfo getUser(String code, String state) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        String accessToken = giteeProvider.getAccessToken(accessTokenDTO);
        GiteeUser giteeUser = giteeProvider.getGiteeUser(accessToken);
        LoginUserInfo loginUserInfo = new LoginUserInfo();
        BeanUtils.copyProperties(giteeUser,loginUserInfo);
        return loginUserInfo;
    }

    @Override
    public String getSupportedType() {
        return "gitee";
    }
}
