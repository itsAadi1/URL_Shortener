package com.itsAadi.urlshortener.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


    @Component
    public class EnvCheck {

        @Value("${DB_URL:missing}")
        private String dbUrl;

        @Value("${DB_USERNAME:missing}")
        private String dbUser;

        @PostConstruct
        public void check() {
            System.out.println("DB_URL=" + dbUrl);
            System.out.println("DB_USERNAME=" + dbUser);
        }
    }

