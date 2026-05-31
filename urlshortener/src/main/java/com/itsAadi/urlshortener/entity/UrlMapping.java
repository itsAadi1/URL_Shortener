package com.itsAadi.urlshortener.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="url_mapping")
@Builder
public class UrlMapping implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false,length = 2048)
    private String originalUrl;
    @Column(nullable = false,unique = true)
    private String shortCode;
    @Builder.Default
    private long clickCount=0L;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;


}
