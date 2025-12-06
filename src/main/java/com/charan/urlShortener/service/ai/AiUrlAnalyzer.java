package com.charan.urlShortener.service.ai;

import com.charan.urlShortener.dto.UrlAnalysisResult;

public interface AiUrlAnalyzer {
    UrlAnalysisResult analyze(String url);
}
