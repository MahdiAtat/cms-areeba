package com.areeba.cms.cmsmircoservice.utils;

import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Component;

@Component
public class EncryptorHolder {

    static BytesEncryptor ENCRYPTOR;

    EncryptorHolder(BytesEncryptor enc) {
        ENCRYPTOR = enc;
    }
}
