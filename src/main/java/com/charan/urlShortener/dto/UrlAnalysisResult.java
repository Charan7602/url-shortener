package com.charan.urlShortener.dto;

import com.charan.urlShortener.model.UrlSafety;

public record UrlAnalysisResult(UrlSafety urlSafety,
                                String safetyReason,
                                String summary) {
}
