package com.example.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class RestTemplateConf {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
