package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.ac_automation.ACAutomation;
import cn.edu.hncj.forum.cache.TagCache;
import cn.edu.hncj.forum.dto.QuestionDTO;
import cn.edu.hncj.forum.exception.CustomizeErrorCode;
import cn.edu.hncj.forum.exception.CustomizeException;
import cn.edu.hncj.forum.model.Question;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.QuestionService;
import cn.edu.hncj.forum.utils.ZKCuratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author FanJian
 */
@Controller
@Slf4j
public class PublishController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private ACAutomation acAutomation;

    @Resource
    private ZKCuratorUtil zkCuratorUtil;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //该路径必须先在zookeeper中创建，否则会报错
    private String lockPath = "/lock/";
    /**
     * 通过编辑按钮完成更新后，发布
     *
     * @param id 问题的id
     * @return 返回页面
     */
    @GetMapping("/publish/{id}")
    public String publish(@PathVariable("id") Long id, Model model) {
        QuestionDTO question = questionService.findById(id);
        // 第一时间把用户传入的信息写入model传递给publish.html，主要是用来做提交失败后，回显的功能
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());
        model.addAttribute("tags", TagCache.get());
        // 隐藏的id,用来判断问题是否已经存在
        model.addAttribute("id", question.getId());
        String uuid = UUID.randomUUID().toString();
        // zookeeper分布式锁的token
        redisTemplate.opsForValue().set("zookeeperToken",uuid,40, TimeUnit.SECONDS);
        model.addAttribute("zookeeperToken", uuid);
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(Model model) {
        model.addAttribute("tags", TagCache.get());
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("zookeeperToken",uuid,40, TimeUnit.SECONDS);
        model.addAttribute("zookeeperToken", uuid);
        return "publish";
    }

    /**
     * 完成问题的发布
     *
     * @param id 问题的id
     */
    @PostMapping("/publish")
    public String doPublish(@RequestParam("title") String title,
                            @RequestParam("description") String description,
                            @RequestParam("tag") String tag,
                            @RequestParam("zookeeperToken") String zookeeperToken,
                            @RequestParam(name = "id", required = false) Long id,
                            HttpServletRequest request,
                            Model model) {
        // 第一时间把用户传入的信息写入model传递给publish.html，主要是用来做提交失败后，回显的功能
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", TagCache.get());

        // 前端的写入校验。
        if (StringUtils.isBlank(title)) {
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if (StringUtils.isBlank(description)) {
            model.addAttribute("error", "问题描述不能为空");
            return "publish";
        }
        if (StringUtils.isBlank(tag)) {
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }

        // StringUtils的split方法会将形如"java,,spring"这样的字符串转换成["java","spring"]这样的字符串数组,直接去掉了逗号之间的""
        String[] split = tag.split( ",");
        if (TagCache.checkBlank(split)) {
            model.addAttribute("error", "传入了空标签");
            return "publish";
        }

        String invalid = TagCache.filterInvalid(split);
        if (StringUtils.isNotBlank(invalid)) {
            model.addAttribute("error", "输入了非法标签" + invalid + "，或者标签中含有空格");
            return "publish";
        }


        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            model.addAttribute("error", "用户未登录");
            return "publish";
        }

        Set<String> strings = acAutomation.containsWords(description);
        if (strings != null && strings.size() > 0) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("包含不文明词汇:");
            for (String str : strings) {
                errorMsg.append(" " + str + " ");
            }
            model.addAttribute("error", errorMsg);
            return "publish";
        }
        if (tryLock(zookeeperToken)) {
            // 如果当前用户为登录状态，则把用户写的问题信息存入到数据库中
            Question question = new Question();
            question.setTitle(title);
            question.setDescription(description);
            question.setTag(tag);
            question.setCreator(user.getId());
            // id是从<input type="hidden" name="id">这个标签传过来的，如果没有经过/publish/{id}这个请求路径（方法），那么这个id就为Null
            question.setId(id);
            questionService.createOrUpdate(question);
        } else {
            throw new CustomizeException(CustomizeErrorCode.CLICK_TOO_FAST);
        }
        return "redirect:/";
    }


    public boolean tryLock(String zookeeperToken) {
        // 用于判断交易唯一性和合法性的Token，在交易执行之前先保存在服务端，
        // 并且下发给客户端，客户端会在执行交易之前把Token带上，没带Token的
        // 请求、Token不存在的服务端的请求、Token不正确的请求都视为非法请求
        String currentLockPath = new StringBuilder(lockPath).append(zookeeperToken).toString();
        boolean isGetLock = false;
        try {
            isGetLock = isGetLock(currentLockPath);
            if (!isGetLock) {
                log.warn("当前交易正在被处理中");
                return false;
            }
            String storedToken = redisTemplate.opsForValue().get("zookeeperToken");
            // 判断Token是否存在且合法
            if (!zookeeperToken.equals(storedToken)) {
                log.warn("指定的Token不存在.");
                return false;
            }
        } catch (Exception e) {
            log.info("处理发生异常", e);
        }
        return true;
    }
    /**
     * 使用在Zookeeper创建临时节点的机制，如果创建成功，则认为其获取锁成功，
     * 如果创建节点失败，则认为锁获取失败。
     * @param currentLockPath 待创建的锁节点
     * @return
     */
    public boolean isGetLock(String currentLockPath) {
        String nowDate = String.valueOf(System.currentTimeMillis());
        try {
            //在Zookeeper创建指定的临时节点，如果节点已经存在了，会抛出异常
            zkCuratorUtil.getZooKeeper().create(currentLockPath, nowDate.getBytes(),
                    ZooDefs.Ids.READ_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
