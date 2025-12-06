package com.charan.urlShortener.service.ai;

import com.charan.urlShortener.dto.AiRequest;
import com.charan.urlShortener.dto.AiResponse;
import com.charan.urlShortener.dto.UrlAnalysisResult;
import com.charan.urlShortener.model.UrlSafety;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;


@Service
public class HttpAiUrlAnalyzer implements AiUrlAnalyzer{
    private final RestTemplate restTemplate;
    private final String aiApiUrl;
    private final String apiKey;

    public HttpAiUrlAnalyzer(@Value("${ai.api.url}") String aiApiUrl,
                             @Value("${ai.api.key")  String apiKey){
        this.restTemplate = new RestTemplate();
        this.aiApiUrl = aiApiUrl;
        this.apiKey = apiKey;
    }
    @Override
    public UrlAnalysisResult analyze(String url){
        try {
            AiRequest request = new AiRequest(url);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<AiRequest> entity = new HttpEntity<>(request,headers);
            ResponseEntity<AiResponse> response = restTemplate.postForEntity(aiApiUrl,entity,AiResponse.class);
            AiResponse body = response.getBody();

            if (body == null){
                return fallBackResult();
            }

            UrlSafety safety = parseSafety(body.getSafety());
            String reason = body.getReason();
            String summary = body.getSummary();

            return new UrlAnalysisResult(safety, reason, summary);
        }catch (Exception e){
            return fallBackResult();
        }
    }

    private UrlSafety parseSafety(String raw){
        if(raw == null) return UrlSafety.UNKNOWN;
        try {
            return UrlSafety.valueOf(raw.toUpperCase(Locale.ROOT));
        }catch (IllegalArgumentException e){
            return UrlSafety.UNKNOWN;
        }
    }

    public UrlAnalysisResult fallBackResult(){
        return new UrlAnalysisResult(
                UrlSafety.UNKNOWN,
                "AI analysis unavailable",
                null
        );
    }
}
