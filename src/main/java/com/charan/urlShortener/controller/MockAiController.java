package com.charan.urlShortener.controller;

import com.charan.urlShortener.dto.AiRequest;
import com.charan.urlShortener.dto.AiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock-ai")
public class MockAiController {
    @PostMapping("/analyze-url")
    public AiResponse analyze(@RequestBody AiRequest aiRequest){
        String url = aiRequest.getUrl();
        String lower = url.toLowerCase();
        String safety,reason,summary;

        if(lower.contains("malware") || lower.contains("phishing") || lower.contains("steal-password")){
            safety = "MALICIOUS";
            reason = "URL contains suspicious keywords often used in phishing or malware sites.";
        }else if (lower.contains("free-gift") || lower.contains("lottery") || lower.contains("win-money")) {
            safety = "SUSPICIOUS";
            reason = "URL contains common scam/lottery patterns. Proceed with caution.";
        } else {
            safety = "SAFE";
            reason = "No obvious malicious or suspicious patterns detected in URL.";
        }

        // Very basic summary for now
        if (lower.contains("amazon")) {
            summary = "Product or listing page hosted on Amazon.";
        } else if (lower.contains("youtube")) {
            summary = "YouTube video or channel link.";
        } else if (lower.contains("linkedin")) {
            summary = "LinkedIn profile, post, or company page.";
        } else {
            summary = "Generic web page; AI mock summary based on URL only.";
        }

        AiResponse response = new AiResponse();
        response.setSafety(safety);
        response.setReason(reason);
        response.setSummary(summary);

        return response;
    }
}
