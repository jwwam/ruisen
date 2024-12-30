/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the rsmanage4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.ruisen.rsmanage.daemon.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruisen.rsmanage.daemon.quartz.entity.SysJob;
import com.ruisen.rsmanage.daemon.quartz.entity.SysJobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


@Mapper
public interface CustomerJobMapper extends BaseMapper<SysJobLog> {
	/**
	 * 查询用户名
	 * @param userId 用户id
	 * @return 查询结果
	 */
	@Select("SELECT username FROM sys_user WHERE user_id = #{userId}")
	String qryUsername(@Param("userId") Long userId);


	/**
	 * 执行自定义SQL
	 * @param sql SQL语句
	 * @return 查询结果
	 */
	@Select("${sql}")
	List<Map<String, Object>> executeCustomSql(@Param("sql") String sql);
}
