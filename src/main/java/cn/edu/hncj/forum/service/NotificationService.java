package cn.edu.hncj.forum.service;

import cn.edu.hncj.forum.dto.NotificationDTO;
import cn.edu.hncj.forum.dto.PaginationDTO;
import cn.edu.hncj.forum.model.User;

public interface NotificationService {
    PaginationDTO list(Long id, Integer page, Integer size);

    Long unreadCount(Long id);

    NotificationDTO read(Long id, User user);
}
