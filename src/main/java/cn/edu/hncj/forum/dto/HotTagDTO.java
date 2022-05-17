package cn.edu.hncj.forum.dto;

import lombok.Data;

/**
 * @Author FanJian
 * @Date 2022/5/12
 */
@Data
public class HotTagDTO implements Comparable{
    private String name;
    private Integer priority;

    @Override
    public int compareTo(Object o) {
        return this.priority - ((HotTagDTO)o).getPriority();
    }
}
