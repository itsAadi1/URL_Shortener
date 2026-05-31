package com.itsAadi.urlshortener.service;

import com.itsAadi.urlshortener.dto.CreateUrlRequest;
import com.itsAadi.urlshortener.dto.CreateUrlResponse;
import com.itsAadi.urlshortener.dto.UrlStatsResponse;

public interface UrlService {
    CreateUrlResponse createShortUrl(CreateUrlRequest createUrlRequest);
    String getOriginalUrl(String shortCode);
    UrlStatsResponse getStats(String shortCode);
}
