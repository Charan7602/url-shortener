package com.charan.urlShortener.dto;

public class AiResponse {
    private String safety;
    private String reason;
    private String summary;

    public AiResponse(){

    }
    public String getSafety(){
        return safety;
    }

    public void setSafety(String safety){
        this.safety = safety;
    }

    public String getReason(){
        return reason;
    }

    public void setReason(String reason){
        this.reason = reason;
    }

    public String getSummary(){
        return summary;
    }

    public void setSummary(String summary){
        this.summary = summary;
    }
}
