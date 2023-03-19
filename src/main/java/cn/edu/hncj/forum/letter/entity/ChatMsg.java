package cn.edu.hncj.forum.letter.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author FanJian
 */
@Data
public class ChatMsg implements Serializable {

	private static final long serialVersionUID = 3611169682695799175L;

	// 发送者的用户id
	private Long senderId;
	// 接受者的用户id
	private Long receiverId;
	// 聊天内容
	private String msg;
	// 发送者的用户昵称
	private String senderName;
	// 接受者的用户昵称
	private String receiverName;
}
