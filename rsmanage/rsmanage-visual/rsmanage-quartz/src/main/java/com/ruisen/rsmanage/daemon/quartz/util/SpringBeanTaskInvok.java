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

package com.ruisen.rsmanage.daemon.quartz.util;

import cn.hutool.core.util.StrUtil;
import com.ruisen.rsmanage.common.core.util.SpringContextHolder;
import com.ruisen.rsmanage.daemon.quartz.constants.RsmanageQuartzEnum;
import com.ruisen.rsmanage.daemon.quartz.entity.SysJob;
import com.ruisen.rsmanage.daemon.quartz.exception.TaskException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 定时任务spring bean反射实现
 *
 * @author 郑健楠
 */
@Component("springBeanTaskInvok")
@Slf4j
public class SpringBeanTaskInvok implements ITaskInvok {

	@Override
	public void invokMethod(SysJob sysJob) throws TaskException {
		Object target;
		Method method;
		Object returnValue;
		
		target = SpringContextHolder.getBean(sysJob.getClassName());
		try {
			// 直接传递整个 SysJob 对象
			method = target.getClass().getDeclaredMethod(sysJob.getMethodName(), SysJob.class);
			ReflectionUtils.makeAccessible(method);
			returnValue = method.invoke(target, sysJob);
			
			if (StrUtil.isEmpty(returnValue.toString()) 
				|| RsmanageQuartzEnum.JOB_LOG_STATUS_FAIL.getType().equals(returnValue.toString())) {
				log.error("定时任务springBeanTaskInvok异常,执行任务：{}", sysJob.getClassName());
				throw new TaskException("定时任务springBeanTaskInvok业务执行失败,任务：" + sysJob.getClassName());
			}
		} catch (NoSuchMethodException e) {
			log.error("定时任务spring bean反射异常方法未找到,执行任务：{}", sysJob.getClassName());
			throw new TaskException("定时任务spring bean反射异常方法未找到,执行任务：" + sysJob.getClassName());
		} catch (Exception e) {
			log.error("定时任务执行异常: {}", e.getMessage());
			throw new TaskException(e.getMessage());
		}
	}

}
