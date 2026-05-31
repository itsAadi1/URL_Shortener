package com.itsAadi.urlshortener.controller;

import com.itsAadi.urlshortener.dto.CreateUrlRequest;
import com.itsAadi.urlshortener.dto.CreateUrlResponse;
import com.itsAadi.urlshortener.dto.UrlStatsResponse;
import com.itsAadi.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
//@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    @PostMapping("/urls")
    public CreateUrlResponse createShortUrl(@Valid @RequestBody CreateUrlRequest request) {
        return urlService.createShortUrl(request);
    }
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode){
        String originalUrl= urlService.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
    @GetMapping("/stats/{shortCode}")
    public UrlStatsResponse getStats( @PathVariable String shortCode) {

        return urlService.getStats(shortCode);
    }
}
