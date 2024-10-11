package com.ruisen.rsmanage.daemon.quartz;

import com.ruisen.rsmanage.common.feign.annotation.EnableRsmanageFeignClients;
import com.ruisen.rsmanage.common.security.annotation.EnableRsmanageResourceServer;
import com.ruisen.rsmanage.common.swagger.annotation.EnableRsmanageDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author frwcloud
 * @date 2023-07-05
 */
@EnableRsmanageDoc("job")
@EnableRsmanageFeignClients
@EnableRsmanageResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class RsmanageQuartzApplication {

	public static void main(String[] args) {
		SpringApplication.run(RsmanageQuartzApplication.class, args);
	}

}
