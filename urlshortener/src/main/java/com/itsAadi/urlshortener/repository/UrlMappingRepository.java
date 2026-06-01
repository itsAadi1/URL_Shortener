package com.itsAadi.urlshortener.repository;

import com.itsAadi.urlshortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping,Long> {
    Optional<UrlMapping> findByShortCode(String shortCode);
    @Transactional
    @Modifying
    @Query("""
            update UrlMapping u
            set u.clickCount=u.clickCount+1
            where u.shortCode=:shortCode
            """)
    void incrementClickCount(String shortCode);
}
