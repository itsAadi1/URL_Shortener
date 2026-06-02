package com.itsAadi.urlshortener.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StartupCheck {

    @Value("${DB_URL:NOT_FOUND}")
    private String dbUrl;

    @PostConstruct
    public void init() {
        System.out.println("DB_URL=" + dbUrl);
    }
}
