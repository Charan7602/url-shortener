package com.charan.urlShortener.service;

import com.charan.urlShortener.dto.UrlSummaryDto;
import com.charan.urlShortener.model.UrlMapping;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
    private final UrlShortenerService urlShortenerService;
    private final StringRedisTemplate stringRedisTemplate;

    public AnalyticsService(StringRedisTemplate stringRedisTemplate, UrlShortenerService urlShortenerService){
        this.stringRedisTemplate = stringRedisTemplate;
        this.urlShortenerService = urlShortenerService;
    }

    private String clicksKey(String shortCode){
        return "clicks::" + shortCode;
    }

    public void incrementClick(String shortCode){
        stringRedisTemplate.opsForValue().increment(clicksKey(shortCode));
    }

    public long getClicks(String shortCode){
        String value = stringRedisTemplate.opsForValue().get(clicksKey(shortCode));
        return value == null ? 0L : Long.parseLong(value);
    }

    public UrlSummaryDto getSummary(String shortCode){
        UrlMapping mapping = urlShortenerService.getActiveMapping(shortCode);
        String key = "clicks::" + shortCode;
        String raw = stringRedisTemplate.opsForValue().get(key);
        long clicks = (raw == null) ? 0L : Long.parseLong(raw);
        return new UrlSummaryDto(
                mapping.getShortCode(),
                mapping.getLongUrl(),
                mapping.getUrlSafety(),
                mapping.getSafetyReason(),
                mapping.getAiSummary(),
                clicks
        );
    }
}
