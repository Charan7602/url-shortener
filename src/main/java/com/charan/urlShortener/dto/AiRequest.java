package com.charan.urlShortener.dto;

public class AiRequest {
    private String url;
    public AiRequest(){};
    public AiRequest(String url){
        this.url = url;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
