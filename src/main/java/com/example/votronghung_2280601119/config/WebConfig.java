package com.example.votronghung_2280601119.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Khai báo với Spring Boot:
        // Nếu có ai truy cập vào đường dẫn có chữ /uploads/...
        // thì hãy vào thư mục "uploads" trong thư mục gốc của project để lấy file ra!
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}