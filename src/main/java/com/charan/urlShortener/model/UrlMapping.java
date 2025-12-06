package com.charan.urlShortener.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.OffsetDateTime;


@Entity
@Table(name = "url_mapping")
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "short_code", nullable = false, unique = true, length = 16)
    private String shortCode;

    @Column(name = "long_url", nullable = false, columnDefinition = "TEXT")
    private String longUrl;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "url_safety")
    private UrlSafety urlSafety = UrlSafety.UNKNOWN;

    @Column(length = 1024)
    private String safetyReason;

    @Column(length = 2048)
    private String aiSummary;

    private Instant lastAnalyzedAt;

    public UrlMapping(){

    }

    public UrlMapping(String shortCode, String longUrl, OffsetDateTime createdAt, OffsetDateTime expiresAt){
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public boolean isActive() {
        return active;
    }

    public Long getId() {
        return Id;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setId(Long id) {
        Id = id;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public UrlSafety getUrlSafety() {
        return urlSafety;
    }

    public Instant getLastAnalyzedAt() {
        return lastAnalyzedAt;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public String getSafetyReason() {
        return safetyReason;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public void setUrlSafety(UrlSafety urlSafety) {
        this.urlSafety = urlSafety;
    }

    public void setLastAnalyzedAt(Instant lastAnalyzedAt) {
        this.lastAnalyzedAt = lastAnalyzedAt;
    }

    public void setSafetyReason(String safetyReason) {
        this.safetyReason = safetyReason;
    }
}
