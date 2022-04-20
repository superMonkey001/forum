package cn.edu.hncj.forum.service;

import cn.edu.hncj.forum.dto.PaginationDTO;
import cn.edu.hncj.forum.dto.QuestionDTO;
import cn.edu.hncj.forum.model.Question;

import java.util.List;


public interface QuestionService {
    PaginationDTO list(Integer page, Integer size);

    /**
     * @param id 当前用户的id
     * @param page 当前页
     * @param size 当前长度
     * @return 返回当前用户所创建的所有问题
     */
    PaginationDTO list(Integer id, Integer page, Integer size);

    QuestionDTO findById(Integer id);

    /**
     * 如果是点击编辑按钮进入publish页面，就是更新
     * 如果是点击发布按钮进入，就是新建
     * @param question
     */
    void createOrUpdate(Question question);
}
