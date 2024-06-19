package io.github.reionchan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * IAM 认证访问管理中心
 *
 * @author Reion
 * @date 2024-06-06
 **/
@SpringBootApplication
//@EnableFeignClients(basePackages = {"io.github.reionchan.rpc.feign"})
@EnableScheduling
//@EnableDiscoveryClient
public class ArchIAMServer {
    public static void main(String[] args) {
        SpringApplication.run(ArchIAMServer.class, args);
    }
}
