package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.AccessTokenDTO;
import cn.edu.hncj.forum.dto.GithubUser;
import cn.edu.hncj.forum.mapper.UserMapper;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.provider.GithubProvider;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

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

    @Autowired
    private UserMapper userMapper;
    /**
     * @param code  Required. The code you received as a response to Step 1.
     * @param state
     * @return
     */
    @GetMapping("/callback")
    public String callback(@RequestParam String code,
                           @RequestParam String state,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(client_id);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClient_secret(client_secret);
        accessTokenDTO.setRedirect_uri(redirect_uri);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getGithubUser(accessToken);
        HttpSession session = request.getSession();
        if (githubUser != null && githubUser.getId() != null) {
            User user = new User();
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setName(githubUser.getName());
            // token:自定义的用户令牌,用来判断数据库中是否有这个用户
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(githubUser.getAvatar_url());
            // 登入成功，写入cookie和session
            // session.setAttribute("user", githubUser);
            // 这一步相当于模拟写入session到数据库
            userMapper.insert(user);
            // 自定义的cookie，传到/路径
            // 自定义cookie的好处，就是在项目重启或宕机之后（session重置），用户刷新页面（但没有重启/退出浏览器）后
            // 用户可以通过存在浏览器中里的token信息（后端IndexController通过token查询数据库）直接登录。而不用手动点击登录按钮
            response.addCookie(new Cookie("token",token));
            return "redirect:/";
        } else {
            // 登入失败，重新登入。执行到这的登入失败只有一种情况，就是超时请求。
            // 因为单纯的账号密码错误在访问https://github.com/login/oauth/authorize这个页面后就已经校验了
            return "redirect:/";
        }
    }
}
