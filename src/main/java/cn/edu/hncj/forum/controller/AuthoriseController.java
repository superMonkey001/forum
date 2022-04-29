package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.UserService;
import cn.edu.hncj.forum.strategy.LoginUserInfo;
import cn.edu.hncj.forum.strategy.UserStrategy;
import cn.edu.hncj.forum.strategy.UserStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@Slf4j
public class AuthoriseController {
    @Autowired
    private UserStrategyFactory userStrategyFactory;

    @Autowired
    private UserService userService;

    @GetMapping("/callback/{type}")
    public String callback(@PathVariable("type") String type,
                            @RequestParam String code,
                            @RequestParam(name = "state", required = false) String state,
                            HttpServletResponse response,
                            HttpServletRequest request) {
        UserStrategy strategy = userStrategyFactory.getStrategy(type);
        LoginUserInfo loginUser = strategy.getUser(code, state);
        if (loginUser != null && loginUser.getId() != null) {
            User user = new User();
            user.setAccountId(String.valueOf(loginUser.getId()));
            user.setName(loginUser.getName());
            // token:自定义的用户令牌,用来判断数据库中是否有这个用户
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setAvatarUrl(loginUser.getAvatar_url());
            user.setType(type);
            // 登入成功，写入cookie和session
            // session.setAttribute("user", githubUser);
            // 这一步相当于模拟写入session到数据库
            userService.createOrUpdate(user);
            // 自定义的cookie，传到/路径
            // 自定义cookie的好处，就是在项目重启或宕机之后（session重置），用户刷新页面（但没有重启/退出浏览器）后
            // 用户可以通过存在浏览器中里的token信息（后端IndexController通过token查询数据库）直接登录。而不用手动点击登录按钮
            Cookie tokenCookie = new Cookie("token", token);
            tokenCookie.setPath("/");
            tokenCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(tokenCookie);
            Cookie[] cookies = request.getCookies();
            // 检测用户是否没有登录就进行评论
            // 因为在js处理逻辑中中noLoginComment这个cookie总是加在commentButNoLogin之前的，
            // 因此不用担心下面这段逻辑是先跳转到question页面，而导致noLoginComment没有加到cookies当中
            if (cookies != null && cookies.length != 0) {
                for (Cookie cookie : cookies) {
                    if ("noLoginComment".equals(cookie.getName())) {
                        // 把评论的内容传给/question/{id}接口
                        String noLoginComment = cookie.getValue();
                        request.getSession().setAttribute("noLoginComment",noLoginComment);
                        // 移除这个cookie
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    } else if ("commentButNoLogin".equals(cookie.getName())) {
                        // 把问题的id传给/question/{id}接口（方法）
                        // 问题的id
                        String questionId = cookie.getValue();
                        // 移除这个cookie
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                        return "redirect:/question/" + questionId;
                    }
                }
            }
            return "redirect:/";
        } else {
            log.error("get git hub error:{}",loginUser);
            // 登入失败，重新登入。执行到这的登入失败只有一种情况，就是超时请求。
            // 因为单纯的账号密码错误在访问https://github.com/login/oauth/authorize这个页面后就已经校验了
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session,
                         HttpServletResponse response) {
        // 清除用户登录态
        session.removeAttribute("user");

        // 清除token
        Cookie cookie = new Cookie("token", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // 返回首页
        return "redirect:/";
    }
}
