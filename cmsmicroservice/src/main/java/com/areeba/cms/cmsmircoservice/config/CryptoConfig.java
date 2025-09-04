package com.areeba.cms.cmsmircoservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

/**
 * Crypto wiring for field-level encryption.
 * <p>
 * Exposes a single {@link BytesEncryptor} using AES/GCM with a per-operation IV.
 * Password and salt come from {@link CryptoProperties}.
 */
@Configuration
public class CryptoConfig {

    /**
     * Creates an {@link BytesEncryptor} backed by AES/GCM.
     * <ul>
     *   <li>IV: secure 12-byte random (recommended size for GCM)</li>
     *   <li>Salt: expected as hex string (stable across restarts)</li>
     *   <li>Mode: {@code GCM} (authenticated encryption)</li>
     * </ul>
     *
     * @param cryptoProperties holds the password and hex salt
     * @return encryptor for use by converters (e.g., {@code AttributeEncryptor})
     */
    @Bean
    BytesEncryptor bytesEncryptor(CryptoProperties cryptoProperties) {
        var ivGen = KeyGenerators.secureRandom(12); // new IV for each encryption
        return new AesBytesEncryptor(
                cryptoProperties.getPassword(),
                cryptoProperties.getSaltHex(),
                ivGen,
                AesBytesEncryptor.CipherAlgorithm.GCM
        );
    }
}
