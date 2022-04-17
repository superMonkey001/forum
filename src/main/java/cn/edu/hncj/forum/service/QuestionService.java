package cn.edu.hncj.forum.service;

import cn.edu.hncj.forum.dto.QuestionDTO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface QuestionService {
    List<QuestionDTO> list();
}
