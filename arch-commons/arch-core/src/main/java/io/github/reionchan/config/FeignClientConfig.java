package io.github.reionchan.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs3.JAXRS3Contract;
import feign.okhttp.OkHttpClient;
import io.github.reionchan.rpc.feign.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * OpenFeign 客户端定制化配置类
 *
 * @author Reion
 * @date 2024-06-04
 **/
@Slf4j
@Configuration
public class FeignClientConfig {

    @Value("${ARCH_USERS_ADDR:http://localhost:8081}")
    private String userServiceId;

    @Bean
    public UserClient userClient(ObjectMapper objectMapper) {
        assert objectMapper != null;
        return Feign.builder()
                .contract(new JAXRS3Contract())
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .logger(new Logger.JavaLogger())
                .logLevel(Logger.Level.FULL)
                .target(UserClient.class, userServiceId);
    }
}
