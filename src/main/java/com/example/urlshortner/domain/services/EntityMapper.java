package com.example.urlshortner.domain.services;

import com.example.urlshortner.domain.entities.ShortUrl;
import com.example.urlshortner.domain.entities.User;
import com.example.urlshortner.domain.models.ShortUrlDto;
import com.example.urlshortner.domain.models.UserDto;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {


    public ShortUrlDto toShortUrlDto(ShortUrl shortUrl) {
        UserDto userDto = null;
        if(shortUrl.getCreatedBy() != null) {
            userDto = toUserDto(shortUrl.getCreatedBy());
        }

        return new ShortUrlDto(
                shortUrl.getId(),
                shortUrl.getShortKey(),
                shortUrl.getOriginalUrl(),
                shortUrl.getPrivate(),
                shortUrl.getExpiresAt(),
                userDto,
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt()
        );
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName());
    }
}
