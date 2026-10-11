package com.example.urlshortner.domain.web;


import com.example.urlshortner.domain.exceptions.ShortUrlNotFoundException;
import com.example.urlshortner.domain.models.ShortUrlDto;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    String handleUrlNotFoundException(ShortUrlNotFoundException ex){
        log.error("Short Url not found: {}",ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    String handleException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return "error/500";
    }

}
