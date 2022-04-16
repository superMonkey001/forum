package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.mapper.UserMapper;
import cn.edu.hncj.forum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
    @Autowired
    private UserMapper userMapper;
    @GetMapping("/")
    public String index(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            return "index";
        }
        for (Cookie cookie : cookies) {
            //如果当前cookie为token
            if("token".equals(cookie.getName())) {
                String token = cookie.getValue();
                User userByToken = userMapper.findUserByToken(token);
                //如果用户换了一个浏览器（重启浏览器），才会导致在有token的情况下，查询的user为null
                if(userByToken != null) {
                    request.getSession().setAttribute("user",userByToken);
                }
                break;
            }
        }
        return "index";
    }
}
