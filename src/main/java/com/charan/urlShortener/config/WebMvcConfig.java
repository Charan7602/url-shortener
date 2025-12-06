package com.charan.urlShortener.config;

import com.charan.urlShortener.rateLimit.ShortenRateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final ShortenRateLimitInterceptor shortenRateLimitInterceptor;

    public WebMvcConfig(ShortenRateLimitInterceptor shortenRateLimitInterceptor){
        this.shortenRateLimitInterceptor = shortenRateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(shortenRateLimitInterceptor).addPathPatterns("/api/shorten/**");
    }
}
