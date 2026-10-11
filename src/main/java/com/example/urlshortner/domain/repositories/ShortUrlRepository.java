package com.example.urlshortner.domain.repositories;

import com.example.urlshortner.domain.entities.ShortUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl,Long> {

    //@Query("select su from ShortUrl su left join fetch su.createdBy where su.isPrivate = false order by su.createdAt desc")
    @Query("select su from ShortUrl su where su.isPrivate = false")
    @EntityGraph(attributePaths = {"createdBy"})
    Page<ShortUrl> findPublicShortUrls(Pageable pageable);

    Page<ShortUrl> findByCreatedById(Long userId, Pageable pageable);

    boolean existsByShortKey(String shortKey);

    @Modifying
    void deleteByIdInAndCreatedById(List<Long> ids, Long userId);

    @Query("select u from ShortUrl u left join fetch u.createdBy")
    Page<ShortUrl> findAllShortUrls(Pageable pageable);

    Optional<ShortUrl> findByShortKey(String shortKey);
}
