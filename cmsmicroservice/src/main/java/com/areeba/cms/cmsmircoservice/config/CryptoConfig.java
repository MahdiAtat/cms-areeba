package com.areeba.cms.cmsmircoservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

@Configuration
public class CryptoConfig {
    @Bean
    BytesEncryptor bytesEncryptor(CryptoProperties cryptoProperties) {
        var ivGen = KeyGenerators.secureRandom(12);
        return new AesBytesEncryptor(cryptoProperties.getPassword(), cryptoProperties.getSaltHex(), ivGen, AesBytesEncryptor.CipherAlgorithm.GCM);
    }
}
