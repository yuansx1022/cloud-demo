package com.atguigu.order.config;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class orderConfig {

    @LoadBalanced//注解是负载均衡
    @Bean
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }
}

