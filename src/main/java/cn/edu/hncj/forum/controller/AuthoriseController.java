package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.AccessTokenDTO;
import cn.edu.hncj.forum.dto.GithubUser;
import cn.edu.hncj.forum.provider.GithubProvider;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 主要处理callback函数（回调redirect-uri携带code）
 * 以及请求https://github.com/login/oauth/access_token路径携带code访问github
 */
@Controller
public class AuthoriseController {
    @Autowired
    private GithubProvider githubProvider;
    /**
     * @param code Required. The code you received as a response to Step 1.
     * @param state
     * @return
     */
    @GetMapping("callback")
    public String callback(String code,String state) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id("7b8e0403167f8fef70f3");
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClient_secret("8d50828f01aeebd03c1c8fc36151c003ccfbf114");
        accessTokenDTO.setRedirect_uri("http://localhost:8887/callback");
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getGithubUser(accessToken);
        System.out.println(githubUser.getName());
        return "index";
    }
}
