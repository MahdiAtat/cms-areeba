package com.areeba.cms.cmsmircoservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "cms.areeba.crypto")
@Configuration
@Data
public class CryptoProperties {
    private String password;
    private String saltHex;
}
