/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the rsmanage4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */

package com.ruisen.rsmanage.admin;

import com.ruisen.rsmanage.common.feign.annotation.EnableRsmanageFeignClients;
import com.ruisen.rsmanage.common.security.annotation.EnableRsmanageResourceServer;
import com.ruisen.rsmanage.common.swagger.annotation.EnableRsmanageDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lengleng
 * @date 2018年06月21日
 * <p>
 * 用户统一管理系统
 */
@EnableRsmanageDoc(value = "admin")
@EnableRsmanageFeignClients
@EnableRsmanageResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class RsmanageAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(RsmanageAdminApplication.class, args);
	}

}
