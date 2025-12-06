package com.charan.urlShortener.rateLimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ShortenRateLimitInterceptor implements HandlerInterceptor {
    private final RateLimiterService rateLimiterService;
    public ShortenRateLimitInterceptor(RateLimiterService rateLimiterService){
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
    {
        System.out.println("Rate Limiter Interceptor function hit");
        String path = request.getRequestURI();
        if(!"POST".equalsIgnoreCase(request.getMethod()) || !path.startsWith("/api/shorten")){
            return true;
        }

        String ClientIp = extractClientIp(request);
        String ClientKey = ClientIp;
        rateLimiterService.consumeToken(ClientKey);
        return true;
    }

    private String extractClientIp(HttpServletRequest request){
        String forwarded = request.getHeader("X-Forwarded-for");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
