package cn.edu.hncj.forum.service;

import cn.edu.hncj.forum.dto.PaginationDTO;


public interface QuestionService {
    PaginationDTO list(Integer page, Integer size);
}
