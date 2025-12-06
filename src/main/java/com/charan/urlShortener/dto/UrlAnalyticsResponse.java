package com.charan.urlShortener.dto;


import java.time.OffsetDateTime;

public record UrlAnalyticsResponse(String shortCode,
                                   String longUrl,
                                   long totalClicks,
                                   OffsetDateTime createdAt,
                                   OffsetDateTime expiresAt) {
}
