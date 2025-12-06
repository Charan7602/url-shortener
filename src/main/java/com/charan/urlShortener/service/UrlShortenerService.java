package com.charan.urlShortener.service;


import com.charan.urlShortener.dto.UrlAnalysisResult;
import com.charan.urlShortener.model.UrlMapping;
import com.charan.urlShortener.model.UrlSafety;
import com.charan.urlShortener.repository.UrlMappingRepository;
import com.charan.urlShortener.service.ai.AiUrlAnalyzer;
import com.charan.urlShortener.util.ShortCodeGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UrlShortenerService {
    private final UrlMappingRepository repository;
    private final ShortCodeGenerator codeGenerator;
    private final AiUrlAnalyzer aiUrlAnalyzer;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public UrlShortenerService(UrlMappingRepository repository, ShortCodeGenerator codeGenerator, AiUrlAnalyzer aiUrlAnalyzer){
        this.repository = repository;
        this.codeGenerator = codeGenerator;
        this.aiUrlAnalyzer = aiUrlAnalyzer;
    }

    @CachePut(
            value = "activeUrlMappings",
            key   = "#result.shortCode",
            condition = "#result != null && #result.active"  // only cache active ones
    )
    @Transactional
    public UrlMapping createShortUrl(String longUrl, Integer expiresInDays){
        validateUrl(longUrl);
        UrlAnalysisResult urlAnalysisResult = aiUrlAnalyzer.analyze(longUrl);
        if (urlAnalysisResult.urlSafety() == UrlSafety.MALICIOUS){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "URL appears malicious: " + urlAnalysisResult.safetyReason()
            );
        }

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiresAt = null;
        if (expiresInDays != null && expiresInDays > 0) {
            expiresAt = now.plus(expiresInDays, ChronoUnit.DAYS);
        }

        String code = generateUniqueCode();
        System.out.println("CREATED shortCode = " + code);
        UrlMapping mapping = new UrlMapping(code,longUrl,now,expiresAt);
        mapping.setUrlSafety(urlAnalysisResult.urlSafety());
        mapping.setSafetyReason(urlAnalysisResult.safetyReason());
        mapping.setAiSummary(urlAnalysisResult.summary());
        mapping.setLastAnalyzedAt(Instant.now());
        mapping.setActive(true);
        return repository.save(mapping);
    }

    @Cacheable(
            value = "activeUrlMappings",  // Redis Cache Name
            key   = "#shortCode"          // Redis key = shortCode
    )
    @Transactional
    public UrlMapping getActiveMapping(String shortCode){
        System.out.println("Hitting DB for shortCode "+ shortCode);
        System.out.println("LOOKUP shortCode = " + shortCode);
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("short url not found"));

        if (!mapping.isActive()) {
            throw new RuntimeException("Short URL is inactive");
        }

        if (mapping.getExpiresAt() != null && mapping.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Short URL expired");
        }

        return mapping;
    }

    @CacheEvict(
            value = "activeUrlMappings",
            key   = "#shortcode"
    )
    public void deactivateMapping(String shortCode){
        repository.findByShortCode(shortCode)
                .ifPresent(mapping -> {
                    mapping.setActive(false);
                    repository.save(mapping);
                });
    }

    public String generateUniqueCode(){
        String code;
        do{
            code = codeGenerator.generateCode();
        }while (repository.findByShortCode(code).isPresent());
        return code;
    }

    public void validateUrl(String longUrl){
        try{
            new URL(longUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + longUrl);
        }
    }

    public String buildShortUrl(String shortCode){
        String path = "/r/" + shortCode;
        return baseUrl.endsWith("/") ? baseUrl.substring(0,baseUrl.length()-1) + path : baseUrl + path;
    }
}
