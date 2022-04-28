package cn.edu.hncj.forum.service;

import cn.edu.hncj.forum.dto.PaginationDTO;
import cn.edu.hncj.forum.dto.QuestionDTO;
import cn.edu.hncj.forum.model.Question;

import java.util.List;


public interface QuestionService {

    PaginationDTO list(String search, Integer page, Integer size);

    /**
     * @param id   当前用户的id
     * @param page 当前页
     * @param size 当前长度
     * @return 返回当前用户所创建的所有问题
     */
    PaginationDTO list(Long id, Integer page, Integer size);

    QuestionDTO findById(Long id);

    /**
     * 如果是点击编辑按钮进入publish页面，就是更新
     * 如果是点击发布按钮进入，就是新建
     *
     * @param question 问题
     */
    void createOrUpdate(Question question);

    /**
     * 根据问题id增加阅读数
     */
    void incView(Long id);

    /**
     * 通过当前问题，查出所有的相关问题
     *
     * @param questionDTO 当前问题
     * @return 相关问题
     */
    List<QuestionDTO> selectRelated(QuestionDTO questionDTO);
}
