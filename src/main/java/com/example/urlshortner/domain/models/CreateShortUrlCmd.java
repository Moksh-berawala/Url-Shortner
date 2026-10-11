package com.example.urlshortner.domain.models;

public record CreateShortUrlCmd(String originalUrl,
                                Boolean isPrivate,
                                Integer expirationInDays,
                                Long userId) {
}
