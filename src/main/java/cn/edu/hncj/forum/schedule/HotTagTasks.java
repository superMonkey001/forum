package cn.edu.hncj.forum.schedule;

import cn.edu.hncj.forum.cache.HotTagCache;
import cn.edu.hncj.forum.mapper.QuestionMapper;
import cn.edu.hncj.forum.model.Question;
import cn.edu.hncj.forum.model.QuestionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author FanJian
 * @Date 2022/5/11
 */
@Component
@Slf4j
public class HotTagTasks {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private HotTagCache hotTagCache;

    /**
     * 定时：三十天
     */
    @Scheduled(fixedRate = 259200000)
    public void hotTagSchedule() {
        log.info("The time is start {}", dateFormat.format(new Date()));
        int offset = 0;
        int limit = 20;

        // 问题的优先级map
        Map<String, Integer> priorities = new HashMap<>();
        // 标签list
        List<Question> questions = new ArrayList<>();
        // 如果当前是第一页，或者不是最后一页(注意：不是最后一页!!!。)(offset == 0，代表当前是第一页；questions.size() == limit，代表当前不是最后一页，或最后一页刚好是满的)
        while (offset == 0 || questions.size() == limit) {
            // 分页查询问题（为什么要分页，而不是直接查询全部呢？）
            questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, limit));
            for (Question question : questions) {
                // 找出当前问题的所有标签
                String[] tags = StringUtils.split(question.getTag(),",");
                for (String tag : tags) {
                    Integer priority = priorities.get(tag);
                    if (priority == null) {
                        priorities.put(tag,5 + question.getCommentCount());
                    } else {
                        priorities.put(tag,priority + 5 + question.getCommentCount());
                    }
                }
            }
            offset += limit;
        }
        // 更新热门话题
        hotTagCache.updateTags(priorities);
        log.info("The time is end {}", dateFormat.format(new Date()));
    }
}
