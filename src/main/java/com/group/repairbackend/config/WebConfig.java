package com.group.repairbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /upload/order/** 映射到磁盘目录 D:/IDEAproject/repair_uploads/order/
        registry.addResourceHandler("/upload/order/**")
                .addResourceLocations("file:D:/IDEAproject/repair_uploads/order/");
    }
}
