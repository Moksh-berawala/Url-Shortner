package com.example.urlshortner.domain.services;


import com.example.urlshortner.ApplicationProperties;
import com.example.urlshortner.domain.entities.ShortUrl;
import com.example.urlshortner.domain.models.CreateShortUrlCmd;
import com.example.urlshortner.domain.models.PagedResult;
import com.example.urlshortner.domain.repositories.ShortUrlRepository;


import com.example.urlshortner.domain.repositories.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.urlshortner.domain.models.ShortUrlDto;
import org.springframework.transaction.annotation.Transactional;


import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Transactional(readOnly = true)
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final EntityMapper entityMapper;
    private final ApplicationProperties properties;
    private final UserRepository userRepository;


    public ShortUrlService(ShortUrlRepository shortUrlRepository, EntityMapper entityMapper, ApplicationProperties properties, UserRepository userRepository) {
        this.shortUrlRepository = shortUrlRepository;
        this.properties = properties;
        this.entityMapper = entityMapper;
        this.userRepository = userRepository;

    }


    public PagedResult<ShortUrlDto> findAllPublicShortUrls(int pageNo, int pageSize) {
        pageNo = pageNo >1? pageNo-1 : 0;
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("createdAt").descending());

        Page<ShortUrlDto> shortUrlDtoPage = shortUrlRepository.findPublicShortUrls(pageable)
                .map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlDtoPage);
    }

    public PagedResult<ShortUrlDto> getUserShortUrls(Long userId, int page, int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        var shortUrlsPage = shortUrlRepository.findByCreatedById(userId, pageable)
                .map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlsPage);
    }

    @Transactional
    public void deleteUserShortUrls(List<Long> ids, Long userId) {
        if (ids != null && !ids.isEmpty() && userId != null) {
            shortUrlRepository.deleteByIdInAndCreatedById(ids, userId);
        }
    }

    public PagedResult<ShortUrlDto> findAllShortUrls(int page, int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        var shortUrlsPage =  shortUrlRepository.findAllShortUrls(pageable).map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlsPage);
    }

    private Pageable getPageable(int page, int size) {
        page = page > 1 ? page - 1: 0;
        return PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
    }

    @Transactional
    public ShortUrlDto createShortUrl(CreateShortUrlCmd cmd) {
        if(properties.validateOriginalUrl()){
            boolean urlExists = UrlExistenceValidator.isUrlExists(cmd.originalUrl());
            if(!urlExists){
                throw new RuntimeException("Original Url Not Exists"+cmd.originalUrl());
            }
        }
        var shortKey =generateUniqueShortKey();
        var shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(cmd.originalUrl());
        shortUrl.setShortKey(shortKey);
        shortUrl.setClickCount(0);
        if(cmd.userId() == null){
            shortUrl.setCreatedBy(null);
            shortUrl.setPrivate(false);
            shortUrl.setExpiresAt(Instant.now().plus(properties.defaultExpiryInDays(), DAYS));
        }else{
            shortUrl.setCreatedBy(userRepository.findById(cmd.userId()).orElseThrow());
            shortUrl.setPrivate(cmd.isPrivate() != null && cmd.isPrivate());
            shortUrl.setExpiresAt(cmd.expirationInDays() !=null ? Instant.now().plus(cmd.expirationInDays(),DAYS):null);
        }
        shortUrl.setCreatedAt(Instant.now());
        shortUrlRepository.save(shortUrl);
        return entityMapper.toShortUrlDto(shortUrl);
    }

    private String generateUniqueShortKey() {
        String shortKey;
        do {
            shortKey = generateRandomShortKey();
        } while (shortUrlRepository.existsByShortKey(shortKey));
        return shortKey;
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_KEY_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomShortKey() {
        StringBuilder sb = new StringBuilder(SHORT_KEY_LENGTH);
        for (int i = 0; i < SHORT_KEY_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Transactional
    public Optional<ShortUrlDto> accessShortUrl(String shortKey, Long userId) {

        ShortUrl shortUrl = shortUrlRepository.findByShortKey(shortKey)
                .orElse(null);

        if (shortUrl == null) {
            return Optional.empty();
        }

        // Check expiration
        if (shortUrl.getExpiresAt() != null
                && shortUrl.getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }

        // Check privacy
        if (Boolean.TRUE.equals(shortUrl.getPrivate())
                && shortUrl.getCreatedBy() != null
                && !Objects.equals(shortUrl.getCreatedBy().getId(), userId)) {
            return Optional.empty();
        }


        shortUrl.setClickCount(shortUrl.getClickCount() + 1);

        shortUrlRepository.save(shortUrl);

        return Optional.of(entityMapper.toShortUrlDto(shortUrl));
    }
}
