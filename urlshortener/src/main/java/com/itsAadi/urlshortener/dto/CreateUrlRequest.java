package com.itsAadi.urlshortener.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class CreateUrlRequest {
    @NotBlank
    @URL
    private String url;
    private String alias;
}
