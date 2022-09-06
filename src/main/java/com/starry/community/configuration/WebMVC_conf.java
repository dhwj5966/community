package com.starry.community.configuration;

import com.starry.community.controller.interceptor.CheckLoginInterceptor;
import com.starry.community.controller.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Starry
 * @create 2022-09-06-12:01 AM
 * @Describe
 */
@Configuration
public class WebMVC_conf implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private CheckLoginInterceptor checkLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);

        registry.addInterceptor(loginInterceptor).excludePathPatterns("/**/*.css", "/**/*.js",
                "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(checkLoginInterceptor).excludePathPatterns("/**/*.css", "/**/*.js",
                "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}
