package cn.edu.hncj.forum.interceptor;

import cn.edu.hncj.forum.cache.HistoryCache;
import cn.edu.hncj.forum.mapper.UserMapper;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.model.UserExample;
import cn.edu.hncj.forum.service.NotificationService;
import cn.edu.hncj.forum.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SessionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Value("${github.client.id}")
    private String githubClientId;

    @Value("${gitee.client.id}")
    private String giteeClientId;

    @Value("${github.redirect.uri}")
    private String githubRedirectUri;

    @Value("${gitee.redirect.uri}")
    private String giteeRedirectUri;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //设置 context 级别的属性
        request.getServletContext().setAttribute("githubClientId", githubClientId);
        request.getServletContext().setAttribute("giteeClientId", giteeClientId);
        request.getServletContext().setAttribute("githubRedirectUri", githubRedirectUri);
        request.getServletContext().setAttribute("giteeRedirectUri", giteeRedirectUri);
        //handler 可能是 RequestResourceHandler springboot 程序 访问静态资源 默认去classpath下的static目录去查询
        if(!(handler instanceof HandlerMethod))
        {
            return true;
        }
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(token)) {
            User user = userService.checkToken(token);
            if (user != null) {
                request.getSession().setAttribute("historyCache",new HistoryCache());
                request.getSession().setAttribute("user", user);
                Long unreadCount = notificationService.unreadCount(user.getId());
                request.getSession().setAttribute("unreadCount", unreadCount);
                return true;
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            for (Cookie cookie : cookies) {
                //如果当前cookie为token
                if ("token".equals(cookie.getName())) {
                    String tokenCookie = cookie.getValue();
                    UserExample userExample = new UserExample();
                    // 传入条件
                    userExample.createCriteria().andTokenEqualTo(tokenCookie);
                    List<User> users = userMapper.selectByExample(userExample);
                    // 如果用户换了一个浏览器（重启浏览器），才会导致在有token的情况下，查询的users为null
                    if (users != null && users.size() != 0) {
                        User loginUser = users.get(0);
                        request.getSession().setAttribute("historyCache",new HistoryCache());
                        request.getSession().setAttribute("user", loginUser);
                        Long unreadCount = notificationService.unreadCount(loginUser.getId());
                        request.getSession().setAttribute("unreadCount", unreadCount);
                    }
                    break;
                }
            }
        }
        // 无论如何都直接放行，只是当没有登录时，在session中没有"user"信息
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
