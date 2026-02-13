package com.asuni.blogservice.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Value("${internal.service-secret}")
    private String internalSecret;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template ->
                template.header("X-INTERNAL-SECRET", internalSecret);
    }
}
