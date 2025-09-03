package com.areeba.cms.cmsmircoservice.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Converter
public class AttributeEncryptor implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String plaintext) {
        if (plaintext == null) return null;
        return Base64.getEncoder().encodeToString(EncryptorHolder.ENCRYPTOR.encrypt(plaintext.getBytes(UTF_8)));
    }

    @Override
    public String convertToEntityAttribute(String columnValue) {
        if (columnValue == null) return null;
        return new String(EncryptorHolder.ENCRYPTOR.decrypt(Base64.getDecoder().decode(columnValue)), UTF_8);
    }
}