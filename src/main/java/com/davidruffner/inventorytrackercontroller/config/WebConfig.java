package com.davidruffner.inventorytrackercontroller.config;

import com.davidruffner.inventorytrackercontroller.controller.PreRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public PreRequestHandler preRequestHandler() {
        return new PreRequestHandler();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(preRequestHandler());
    }
}
