package com.antiquelife.antiquelifebackend.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Дозволити доступ до всіх адрес
                .allowedOrigins("http://localhost:5173",
                "https://antiquelife.onrender.com/") // Твій Vue.js порт (перевір, чи він саме такий)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Дозволені методи
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}