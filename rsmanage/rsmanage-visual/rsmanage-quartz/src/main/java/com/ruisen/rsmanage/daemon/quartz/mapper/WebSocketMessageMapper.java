package com.ruisen.rsmanage.daemon.quartz.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruisen.rsmanage.daemon.quartz.entity.WebSocketMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface WebSocketMessageMapper extends BaseMapper<WebSocketMessage> {

//	@Select("SELECT * FROM web_socket_message WHERE user_id = #{userId} AND is_read = false ORDER BY create_time ASC")
//	List<WebSocketMessage> selectUnreadMessages(Long userId);
}
