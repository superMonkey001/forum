package cn.edu.hncj.forum.cache;

import cn.edu.hncj.forum.dto.HotTagDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author FanJian
 * @Date 2022/5/12
 */
@Component
@Data
public class HotTagCache {
    private List<String> hotTagList = new ArrayList<>();

    /**
     * 更新热门话题
     * @param tags key：标签名,value： 优先级
     */
    public void updateTags(Map<String,Integer> tags) {
        int max = 10;
        // 小顶堆
        PriorityQueue<HotTagDTO> priorityQueue = new PriorityQueue<>(max);
        tags.forEach((tag,priority) -> {
            HotTagDTO hotTagDTO = new HotTagDTO();
            hotTagDTO.setName(tag);
            hotTagDTO.setPriority(priority);
            // 如果热门标签不到max个，就直接把标签放入队列中
            if (priorityQueue.size() < max) {
                priorityQueue.add(hotTagDTO);
            } else {
                HotTagDTO minHotTagDTO = priorityQueue.peek();
                // 如果当前遍历到的标签的热度比热门标签队列中的头部（相对最不热的标签）大的话，那么就将头部去除，并且把当前标签放置头部
                if (hotTagDTO.getPriority().compareTo(minHotTagDTO.getPriority()) > 0) {
                    priorityQueue.poll();
                    priorityQueue.add(hotTagDTO);
                }
            }
        });
        // 用来存放热门标签的list
        List<String> sortedTags = new ArrayList<>();
        HotTagDTO poll = priorityQueue.poll();
        while (poll != null) {
            sortedTags.add(poll.getName());
            poll = priorityQueue.poll();
        }
        hotTagList = sortedTags;
    }
}
