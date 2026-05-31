package com.itsAadi.urlshortener.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUrlResponse {
    private String shortCode;
    private String shortUrl;
}
