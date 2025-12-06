package com.charan.urlShortener.dto;

import com.charan.urlShortener.model.UrlSafety;

public record UrlSummaryDto(
        String shortCode,
        String longUrl,
        UrlSafety urlSafety,
        String safetyReason,
        String aiSummary,
        long clickCount
) {
}
