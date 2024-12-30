package com.ruisen.rsmanage.daemon.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import java.time.LocalDateTime;
@Data
@TableName("sys_websocket_message")
public class WebSocketMessage {

	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 消息内容
	 */
	private String content;

	/**
	 * 是否已读
	 */
	private Boolean isRead;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
}
