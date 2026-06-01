package com.itsAadi.urlshortener.service;

import com.itsAadi.urlshortener.dto.CreateUrlRequest;
import com.itsAadi.urlshortener.dto.CreateUrlResponse;
import com.itsAadi.urlshortener.dto.UrlStatsResponse;
import com.itsAadi.urlshortener.entity.UrlMapping;
import com.itsAadi.urlshortener.repository.UrlMappingRepository;
import com.itsAadi.urlshortener.util.AliasAlreadyExistsException;
import com.itsAadi.urlshortener.util.ShortCodeGenerator;
import com.itsAadi.urlshortener.util.UrlNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class UrlServiceImpl implements UrlService {
    private final UrlMappingRepository urlMappingRepository;
    private final UrlCacheService urlCacheService;
    @Value("${app.base-url}")
    private String baseUrl;
    @Override
    public CreateUrlResponse createShortUrl(CreateUrlRequest createUrlRequest) {
        String shortCode;
        if(createUrlRequest.getAlias()!=null){
            if(urlMappingRepository.findByShortCode(createUrlRequest.getAlias()).isPresent()){
                throw new AliasAlreadyExistsException("Alias already exists");
            }
            else{
                shortCode=createUrlRequest.getAlias();
            }
        }
        else {
            do {
                shortCode = ShortCodeGenerator.generate();
            } while (urlMappingRepository.findByShortCode(shortCode).isPresent());
        }
        UrlMapping mapping = UrlMapping.builder()
                .originalUrl(createUrlRequest.getUrl())
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .build();
        urlMappingRepository.save(mapping);
        return CreateUrlResponse.builder()
                .shortCode(mapping.getShortCode())
                .shortUrl(baseUrl+"/"+mapping.getShortCode())
                .build();
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        String originalUrl=urlCacheService.getUrlMapping(shortCode);
        urlMappingRepository.incrementClickCount(shortCode);
        return originalUrl;
    }
    @Override
    public UrlStatsResponse getStats(String shortCode) {

        UrlMapping mapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException(
                                "Short URL not found"));

        return UrlStatsResponse.builder()
                .originalUrl(mapping.getOriginalUrl())
                .shortCode(mapping.getShortCode())
                .clickCount(mapping.getClickCount())
                .createdAt(mapping.getCreatedAt())
                .build();
    }
}
