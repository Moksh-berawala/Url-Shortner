package com.example.urlshortner.domain.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name="short_urls")
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "short_urls_id_gen")
    @SequenceGenerator(name = "short_urls_id_gen",sequenceName = "short_urls_id_seq",allocationSize=1)
    @Column(name ="id",nullable = false)
    private Long id;

    @Column(unique = true,nullable = false,length = 10)
    private String shortKey;

    @Column(name = "original_url",nullable = false,length = Integer.MAX_VALUE)
    private String originalUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "Created_By")
    private User createdBy;

    @ColumnDefault("false")
    @Column(name = "isPrivate",nullable = false)
    private Boolean isPrivate;

    @ColumnDefault("0")
    @Column(name ="click_count",nullable = false)
    private Long clickCount;

    @Column(name = "expiresAt")
    private Instant expiresAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at",nullable = false)
    private Instant createdAt;

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public long getClickCount() {
        return clickCount;
    }

    public void setClickCount(long clickCount) {
        this.clickCount = clickCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortKey() {
        return shortKey;
    }

    public void setShortKey(String shortKey) {
        this.shortKey = shortKey;
    }



    public Long getId() {
        return id;
    }
}
