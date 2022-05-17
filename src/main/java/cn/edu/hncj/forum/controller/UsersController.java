package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.ResultDTO;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.UserService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

/**
 * @Author FanJian
 * @Date 2022/5/16
 */

@Controller
@RequestMapping("/users")
@Slf4j
public class UsersController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @ResponseBody
    @GetMapping("/currentUser")
    public ResultDTO currentUser(@RequestHeader("Authorization") String token) {
        log.info("token==============================={}",token);
        return ResultDTO.okOf("当前用户token",token);
    }
}
