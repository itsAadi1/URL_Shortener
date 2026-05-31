package com.itsAadi.urlshortener.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUrlRequest {
    @NotBlank
    private String url;
    private String alias;
}
