package com.charan.urlShortener.controller;

import com.charan.urlShortener.dto.ShortenRequest;
import com.charan.urlShortener.dto.ShortenResponse;
import com.charan.urlShortener.dto.UrlAnalyticsResponse;
import com.charan.urlShortener.dto.UrlSummaryDto;
import com.charan.urlShortener.model.UrlMapping;
import com.charan.urlShortener.service.AnalyticsService;
import com.charan.urlShortener.service.UrlShortenerService;
import com.charan.urlShortener.service.ai.AiUrlAnalyzer;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class UrlShortenerController {
    private final UrlShortenerService service;
    private final AnalyticsService analyticsService;

    public UrlShortenerController(UrlShortenerService service, AnalyticsService analyticsService){
        this.service = service;
        this.analyticsService = analyticsService;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponse> shorten(@RequestBody ShortenRequest shortenRequest){
        UrlMapping urlMapping = service.createShortUrl(shortenRequest.getLongUrl(),shortenRequest.getExpiresInDays());
        String shortUrl = service.buildShortUrl(urlMapping.getShortCode());
        ShortenResponse response = new ShortenResponse(shortUrl,urlMapping.getShortCode());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public String home(){
        return "URL Shortener Application is running";
    }
    @GetMapping("/r/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException{
        UrlMapping mapping = service.getActiveMapping(shortCode);
        if (mapping == null) {
            return;
        }
        analyticsService.incrementClick(shortCode);
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", mapping.getLongUrl());
    }

    @GetMapping("/analytics/{shortCode}")
    public ResponseEntity<UrlAnalyticsResponse> getAnalytics(@PathVariable String shortCode){
        UrlMapping mapping = service.getActiveMapping(shortCode);
        if(mapping == null){
            return ResponseEntity.notFound().build();
        }

        long clicks = analyticsService.getClicks(shortCode);
        UrlAnalyticsResponse response = new UrlAnalyticsResponse(
                mapping.getShortCode(),
                mapping.getLongUrl(),
                clicks,
                mapping.getCreatedAt(),
                mapping.getExpiresAt()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/url/{shortCode}")
    public ResponseEntity<Void> delete(@PathVariable String shortCode) {
        service.deactivateMapping(shortCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/{shortCode}")
    public UrlSummaryDto getSummary(@PathVariable String shortCode){
        return analyticsService.getSummary(shortCode);
    }
}
