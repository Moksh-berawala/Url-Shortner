package com.example.urlshortner.domain.models;

public record CreateUserCmd(String email,
                           String password,
                           String name,
                           Role role) {
}
