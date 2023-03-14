package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.CustomUserDTO;
import cn.edu.hncj.forum.dto.ForgetDTO;
import cn.edu.hncj.forum.dto.ResultDTO;
import cn.edu.hncj.forum.exception.CustomizeErrorCode;
import cn.edu.hncj.forum.exception.CustomizeException;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.UserService;
import cn.edu.hncj.forum.utils.JWTUtils;
import cn.edu.hncj.forum.utils.MailUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author FanJian
 * @Date 2022/5/14
 */
@Controller
@RequestMapping("login")
public class LoginController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserService userService;

    @GetMapping("forget")
    public String forget() {
        return "forget";
    }

    @PostMapping
    @ResponseBody
    public ResultDTO login(@RequestBody CustomUserDTO customUserDTO, @RequestHeader("Authorization") String token) {
        // 先走缓存，如果缓存中有数据，直接返回结果
        User redisUser = userService.checkToken(token);
        if (redisUser != null) {
            return ResultDTO.okOf("登录成功", token);
        }
        String name = customUserDTO.getName();
        String password = customUserDTO.getPassword();
        if (StringUtils.isBlank(name) || StringUtils.isBlank(password)) {
            return ResultDTO.errorOf(CustomizeErrorCode.USER_OR_PASSWORD_ERROR);
        }
        User userByName = userService.selectByName(name);
        if (userByName == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.USER_OR_PASSWORD_ERROR);
        }
        String salt = userByName.getSalt();
        String dbPassword = DigestUtils.md5Hex(password + salt);
        User user = userService.findUserByNameAndPassword(name, dbPassword);
        if (user != null) {
            String newToken = JWTUtils.createToken(user.getId());
            /// System.out.println("后台token=================:"+newToken);
            redisTemplate.opsForValue().set("TOKEN_" + newToken, JSON.toJSONString(user), 1, TimeUnit.DAYS);
            return ResultDTO.okOf("登录成功", newToken);
        }
        return ResultDTO.errorOf(CustomizeErrorCode.USER_OR_PASSWORD_ERROR);
    }


    @ResponseBody
    @PutMapping("password")
    public ResultDTO password(@RequestBody ForgetDTO forgetDTO) {
        String name = forgetDTO.getName();
        String email = forgetDTO.getEmail();
        String checkCode = forgetDTO.getCheckCode();
        String password = forgetDTO.getPassword();
        String redisCheckCode = redisTemplate.opsForValue().get("CHECKCODE_" + email);
        if (StringUtils.isBlank(email) || StringUtils.isBlank(checkCode)) {
            throw new CustomizeException(CustomizeErrorCode.FORGET_PASSWORD_PARAM_NOT_NULL);
        }
        if (checkCode.equals(redisCheckCode)) {
            User user = userService.selectByName(name);
            if (user == null) {
                throw new CustomizeException(CustomizeErrorCode.USER_NOT_FOUND);
            } else {
                User updateUser = new User();
                updateUser.setId(user.getId());
                String salt = user.getSalt();
                password = DigestUtils.md5Hex(password + salt);
                updateUser.setPassword(password);
                userService.updatePassword(updateUser);
                return ResultDTO.okOf();
            }
        }
        return ResultDTO.errorOf(444, "验证码错误");
    }

    @ResponseBody
    @GetMapping("/sendEmail")
    public ResultDTO sendEmail(@RequestParam String email) {
        String checkCode = UUID.randomUUID().toString().substring(0, 6);
        MailUtils.sendMail(email, "您的验证码为" + checkCode + "验证码有效期为一分钟,请尽快修改", "修改密码");
        redisTemplate.opsForValue().set("CHECKCODE_" + email, checkCode, 60, TimeUnit.SECONDS);
        return ResultDTO.okOf();
    }

    @ResponseBody
    @PostMapping("/register")
    public ResultDTO register(@RequestBody CustomUserDTO customUserDTO) {
        String name = customUserDTO.getName();
        String password = customUserDTO.getPassword();
        String avatarUrl = customUserDTO.getAvatar_url();
        String email = customUserDTO.getEmail();
        if (StringUtils.isBlank(name) || StringUtils.isBlank(password) || StringUtils.isBlank(email)) {
            throw new CustomizeException(CustomizeErrorCode.REGISTER_PARAM_NOT_NULL);
        }
        User dbUser = userService.findUserByName(name);
        if (dbUser != null) {
            throw new CustomizeException(CustomizeErrorCode.USER_IS_EXIST);
        }
        String dbEmail = userService.findEmail(email);
        if (StringUtils.isNotBlank(dbEmail)) {
            throw new CustomizeException(CustomizeErrorCode.EMAIL_IS_USED);
        }
        User user = new User();
        user.setName(name);
        user.setAvatarUrl(avatarUrl);
        user.setEmail(email);
        user.setPassword(password);
        String token = userService.createUserAndReturnToken(user);
        return ResultDTO.okOf("注册成功", token);
    }
}
