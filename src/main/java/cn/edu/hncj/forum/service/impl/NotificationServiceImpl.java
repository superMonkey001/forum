package cn.edu.hncj.forum.service.impl;

import cn.edu.hncj.forum.dto.NotificationDTO;
import cn.edu.hncj.forum.dto.PaginationDTO;
import cn.edu.hncj.forum.enums.NotificationStatusEnum;
import cn.edu.hncj.forum.enums.NotificationTypeEnum;
import cn.edu.hncj.forum.exception.CustomizeErrorCode;
import cn.edu.hncj.forum.exception.CustomizeException;
import cn.edu.hncj.forum.mapper.NotificationMapper;
import cn.edu.hncj.forum.mapper.UserMapper;
import cn.edu.hncj.forum.model.Notification;
import cn.edu.hncj.forum.model.NotificationExample;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.NotificationService;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author FanJian
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查看个人的所有通知
     * 通过用户id返回通知页面信息
     *
     * @param id   当前用户（接收通知的用户）的id
     * @param page 当前页
     * @param size 当前长度
     * @return
     */
    @Override
    public PaginationDTO list(Long id, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        Integer totalPage;
        // 一共有多少问题
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.setOrderByClause("gmt_create desc");
        notificationExample.createCriteria().andReceiverEqualTo(id);
        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);
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
        NotificationExample notificationExample1 = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(id);
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample1, new RowBounds(offset, size));

        // 如果没有通知，那么就没必要进行下面的封装
        if (notifications.size() == 0) {
            return paginationDTO;
        }

        // 将notification封装成notificationDTO
        List<NotificationDTO> notificationDTOS = notifications.stream().map(notification -> {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            return notificationDTO;
        }).collect(Collectors.toList());

        paginationDTO.setData(notificationDTOS);

        return paginationDTO;
    }

    /**
     * 通过用户id查找用户的未读消息总数
     * @param id 用户（接受消息的用户）id
     * @return 未读的回复数
     */
    @Override
    public Long unreadCount(Long id) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus()).andReceiverEqualTo(id);
        long unreadCount = notificationMapper.countByExample(notificationExample);
        return unreadCount;
    }

    /**
     * 通过通知的id查找通知，并更新通知，并校验是不是自己读的通知
     * @param id 通知的id
     * @param user 当前登录的用户
     * @return 带有TypeName和outerTitle的NotificationDTO通知对象
     */
    @Override
    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);

        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }

        if (!notification.getReceiver().equals(user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
