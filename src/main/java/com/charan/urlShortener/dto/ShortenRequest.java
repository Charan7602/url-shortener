package com.charan.urlShortener.dto;


public class ShortenRequest {
    private String longUrl;
    private Integer expiresInDays;

    public ShortenRequest(){

    }

    public String getLongUrl() {
        return longUrl;
    }

    public Integer getExpiresInDays() {
        return expiresInDays;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public void setExpiresInDays(Integer expiresInDays) {
        this.expiresInDays = expiresInDays;
    }
}
