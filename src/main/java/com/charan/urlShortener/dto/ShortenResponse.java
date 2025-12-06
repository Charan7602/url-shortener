package com.charan.urlShortener.dto;


public class ShortenResponse {
    private String shortUrl;
    private String shortCode;

    public ShortenResponse(String shortUrl,String shortCode){
        this.shortUrl = shortUrl;
        this.shortCode = shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
