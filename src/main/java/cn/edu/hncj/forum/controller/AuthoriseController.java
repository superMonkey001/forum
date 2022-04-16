package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.AccessTokenDTO;
import cn.edu.hncj.forum.dto.GithubUser;
import cn.edu.hncj.forum.provider.GithubProvider;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 主要处理callback函数（回调redirect-uri携带code）
 * 以及请求https://github.com/login/oauth/access_token路径携带code访问github
 */
@Controller
public class AuthoriseController {
    @Autowired
    private GithubProvider githubProvider;
    @Value("${github.client.id}")
    private String client_id;
    @Value("${github.client.secret}")
    private String client_secret;
    @Value("${github.redirect.uri}")
    private String redirect_uri;

    /**
     * @param code Required. The code you received as a response to Step 1.
     * @param state
     * @return
     */
    @GetMapping("callback")
    public String callback(@RequestParam String code,@RequestParam String state) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(client_id);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClient_secret(client_secret);
        accessTokenDTO.setRedirect_uri(redirect_uri);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getGithubUser(accessToken);
        System.out.println(githubUser.getName());
        return "index";
    }
}
