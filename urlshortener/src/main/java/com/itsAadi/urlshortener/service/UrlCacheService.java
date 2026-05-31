package com.itsAadi.urlshortener.service;

import com.itsAadi.urlshortener.entity.UrlMapping;
import com.itsAadi.urlshortener.repository.UrlMappingRepository;
import com.itsAadi.urlshortener.util.UrlNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlCacheService {
    private final UrlMappingRepository urlMappingRepository;
    @Cacheable(
            value="urls",
            key="#shortCode"
    )
    public UrlMapping getUrlMapping(String shortCode) {
        return urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(()-> new UrlNotFoundException("Short URL Not Found"));
    }
}
