package cn.edu.hncj.forum.service.impl;

import cn.edu.hncj.forum.dto.PaginationDTO;
import cn.edu.hncj.forum.dto.QuestionDTO;
import cn.edu.hncj.forum.mapper.QuestionMapper;
import cn.edu.hncj.forum.mapper.UserMapper;
import cn.edu.hncj.forum.model.Question;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询到所有的QuestionDTO(包括User信息)
     * @param page 当前页
     * @param size 当前长度
     * @return 返回当前页面的所有信息(questions, page, pages)
     */
    @Override
    public PaginationDTO list(Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        // 一共有多少问题
        Integer totalCount = questionMapper.count();
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        // 因为在IndexController中执行了该方法，并且传入了pagination到index.html，所以要校验
        // PaginationDTO pagination = questionService.list(page, size);
        // model.addAttribute("pagination", pagination);
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage, page);

        Integer offset = size * (page - 1);
        List<Question> list = questionMapper.list(offset, size);
        List<QuestionDTO> questionDTOS = new ArrayList<>();

        for (Question question : list) {
            Integer creatorId = question.getCreator();
            QuestionDTO questionDTO = copy(question);
            User user = userMapper.findById(creatorId);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOS);

        return paginationDTO;
    }

    /**
     * 查看个人发布的所有问题
     * 通过用户id返回页面信息
     * @param id   当前用户的id
     * @param page 当前页
     * @param size 当前长度
     * @return
     */
    @Override
    public PaginationDTO list(Integer id, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        // 一共有多少问题
        Integer totalCount = questionMapper.countByCreator(id);
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = size * (page - 1);
        List<Question> list = questionMapper.listByCreator(id, offset, size);
        List<QuestionDTO> questionDTOS = new ArrayList<>();

        for (Question question : list) {
            Integer creatorId = question.getCreator();
            QuestionDTO questionDTO = copy(question);
            User user = userMapper.findById(creatorId);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOS);

        return paginationDTO;
    }

    /**
     * 通过id查找问题
     * @param id
     * @return
     */
    @Override
    public QuestionDTO findById(Integer id) {
        Question question = questionMapper.findById(id);
        User user = userMapper.findById(question.getCreator());
        QuestionDTO questionDTO = copy(question);
        questionDTO.setUser(user);
        return questionDTO;
    }

    /**
     * Question->QuestionDTO的封装
     */
    public QuestionDTO copy(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        return questionDTO;
    }
}
