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
     *
     * @param page
     * @param size
     * @return 返回当前页面的所有信息(questions, page, pages)
     */
    @Override
    public PaginationDTO list(Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        // 一共有多少问题
        Integer totalCount = questionMapper.count();
        paginationDTO.setPagination(totalCount, page, size);

        // 因为在IndexController中执行了该方法，并且传入了pagination到index.html，所以要校验
        // PaginationDTO pagination = questionService.list(page, size);
        // model.addAttribute("pagination", pagination);
        if(page < 1) {
            page = 1;
        }
        Integer totalPage = paginationDTO.getTotalPage();
        if(page > totalPage) {
            page = totalPage;
        }

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
     * Question->QuestionDTO的封装
     */
    public QuestionDTO copy(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        return questionDTO;
    }
}
