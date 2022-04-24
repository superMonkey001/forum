package cn.edu.hncj.forum.service.impl;

import cn.edu.hncj.forum.dto.PaginationDTO;
import cn.edu.hncj.forum.dto.QuestionDTO;
import cn.edu.hncj.forum.exception.CustomizeErrorCode;
import cn.edu.hncj.forum.exception.CustomizeException;
import cn.edu.hncj.forum.mapper.QuestionExtMapper;
import cn.edu.hncj.forum.mapper.QuestionMapper;
import cn.edu.hncj.forum.mapper.UserMapper;
import cn.edu.hncj.forum.model.Question;
import cn.edu.hncj.forum.model.QuestionExample;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.QuestionService;
import org.apache.ibatis.session.RowBounds;
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
    private QuestionExtMapper questionExtMapper;

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
        Integer totalCount = (int)questionMapper.countByExample(new QuestionExample());
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

        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));
        List<QuestionDTO> questionDTOS = new ArrayList<>();

        for (Question question : questions) {
            Long creatorId = question.getCreator();
            QuestionDTO questionDTO = copy(question);
            User user = userMapper.selectByPrimaryKey(creatorId);
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
    public PaginationDTO list(Long id, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        // 一共有多少问题
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(id);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);
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
        QuestionExample questionExample1 = new QuestionExample();
        questionExample1.createCriteria().andCreatorEqualTo(id);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample1, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOS = new ArrayList<>();

        for (Question question : questions) {
            Long creatorId = question.getCreator();
            QuestionDTO questionDTO = copy(question);
            User user = userMapper.selectByPrimaryKey(creatorId);
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
    public QuestionDTO findById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO = copy(question);
        questionDTO.setUser(user);
        return questionDTO;
    }


    @Override
    public void createOrUpdate(Question question) {
        // 如果/publish路径后面没有/id子路径，那么就是通过点击发布按钮新建问题，而不是点击编辑按钮更改问题
        if(question.getId() == null) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }else {
            Question updateQuestion = new Question();
            updateQuestion.setId(question.getId());
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            // 考虑一个场景，用户打开两个页面，一个页面在编辑问题，而另一个页面执行了删除问题的操作，
            // 那么这个问题将不存在，执行更新操作会返回0
            int updated = questionMapper.updateByPrimaryKeySelective(updateQuestion);
            // 更新失败
            if(updated == 0) {
                // 抛出问题不存在异常
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    @Override
    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        // 做缓存用?
        question.setViewCount(1);
        questionExtMapper.incView(question);
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
