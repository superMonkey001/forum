package cn.edu.hncj.forum.service.impl;

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
     * @return
     */
    @Override
    public List<QuestionDTO> list() {
        List<Question> list = questionMapper.list();
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        for (Question question : list) {
            Integer creatorId = question.getCreator();
            QuestionDTO questionDTO = copy(question);
            User user = userMapper.findById(creatorId);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        return questionDTOS;
    }

    /**
     * Question->QuestionDTO的封装
     */
    public QuestionDTO copy(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        return questionDTO;
    }
}
