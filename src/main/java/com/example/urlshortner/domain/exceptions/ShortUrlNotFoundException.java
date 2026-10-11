package com.example.urlshortner.domain.exceptions;

public class ShortUrlNotFoundException extends RuntimeException {
    public ShortUrlNotFoundException(String message) {
        super(message);
    }
}
