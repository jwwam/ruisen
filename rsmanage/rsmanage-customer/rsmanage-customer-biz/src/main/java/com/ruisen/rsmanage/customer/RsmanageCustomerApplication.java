package com.ruisen.rsmanage.customer;

import com.ruisen.rsmanage.common.security.annotation.EnableRsmanageResourceServer;
import com.ruisen.rsmanage.common.swagger.annotation.EnableRsmanageDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
* @author pig archetype
* <p>
* 项目启动类
*/
@EnableRsmanageDoc("rsmanage")
@EnableRsmanageResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class RsmanageCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RsmanageCustomerApplication.class, args);
    }

}
