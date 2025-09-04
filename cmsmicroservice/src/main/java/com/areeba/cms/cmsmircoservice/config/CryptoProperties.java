package com.areeba.cms.cmsmircoservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds crypto settings under the prefix {@code cms.areeba.crypto}.
 *
 * <p>Used by {@link CryptoConfig#bytesEncryptor(CryptoProperties)} to build the AES/GCM
 * {@code BytesEncryptor} for field-level encryption.</p>
 */
@Configuration
@ConfigurationProperties(prefix = "cms.areeba.crypto")
@Data
public class CryptoProperties {

    /** Secret passphrase used for key derivation. */
    private String password;

    /** Hex-encoded salt used for key derivation; should be stable across restarts. */
    private String saltHex;
}
