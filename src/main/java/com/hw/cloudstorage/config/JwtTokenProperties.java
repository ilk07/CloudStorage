package com.hw.cloudstorage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt.token")
public class JwtTokenProperties {
    private String secret;
    private long expired;
    private String header;
    private String bearer;
    private String issuer;
}
