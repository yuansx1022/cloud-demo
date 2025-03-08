package com.atguigu.order.config;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class orderConfig {

    @Bean
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }
}

