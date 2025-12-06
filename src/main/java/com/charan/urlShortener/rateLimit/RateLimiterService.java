package com.charan.urlShortener.rateLimit;

import com.charan.urlShortener.exception.RateLimitExceededException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {
    private static final int MAX_REQUESTS_PER_MINUTE = 20;
    private final StringRedisTemplate redisTemplate;

    public RateLimiterService(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public void consumeToken(String clientKey){
        String key = "rl:shorten" + clientKey;
        System.out.println("Rate Limiter service hit");
        Long current = redisTemplate.opsForValue().increment(key);
        if(current != null && current == 1L){
            redisTemplate.expire(key, Duration.ofSeconds(60));
        }

        System.out.println("current: " + current);
        if(current != null && current > MAX_REQUESTS_PER_MINUTE){
            throw new RateLimitExceededException("Too many requests. Please try again after some time");
        }
    }
}
