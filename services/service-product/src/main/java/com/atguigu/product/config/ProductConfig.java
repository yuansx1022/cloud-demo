package com.atguigu.product.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ProductConfig {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
