package com.areeba.cms.cmsmircoservice.utils;

import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Component;

/**
 * Holds a shared {@link BytesEncryptor} for spots that aren’t Spring-managed
 * (e.g., JPA {@code AttributeConverter}s).
 * <p>Spring wires the encryptor once at startup; the static reference lets
 * converters reach it without DI.</p>
 */
@Component
public class EncryptorHolder {

    /** Shared encryptor instance, set once at application start. */
    static BytesEncryptor ENCRYPTOR;

    /**
     * Constructor injection by Spring. Assigns the static reference used by
     * non-managed code.
     *
     * @param enc the application’s encryptor
     */
    EncryptorHolder(BytesEncryptor enc) {
        ENCRYPTOR = enc;
    }
}
