package com.areeba.cms.cmsmircoservice.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * JPA converter that transparently <b>encrypts</b> String fields on write and <b>decrypts</b> on read.
 * <p>
 * DB stores a Base64-encoded ciphertext; the entity exposes plaintext.
 * Use on a field with:
 * <pre>{@code
 *   @Convert(converter = AttributeEncryptor.class)
 *   private String cardNumber;
 * }</pre>
 *
 * Notes:
 * <ul>
 *   <li>Null-in → null-out (no encryption for nulls).</li>
 *   <li>Assumes {@code EncryptorHolder.ENCRYPTOR} is properly initialized and thread-safe.</li>
 *   <li>Base64 adds size overhead; choose column length accordingly.</li>
 *   <li>Encrypted data isn’t directly searchable/sortable without extra design (hash columns, etc.).</li>
 * </ul>
 */
@Converter
public class AttributeEncryptor implements AttributeConverter<String, String> {

    /**
     * Converts plaintext to Base64-encoded ciphertext for storage.
     *
     * @param plaintext plain String (may be {@code null})
     * @return Base64 ciphertext or {@code null}
     */
    @Override
    public String convertToDatabaseColumn(String plaintext) {
        if (plaintext == null) return null;
        return Base64.getEncoder()
                .encodeToString(EncryptorHolder.ENCRYPTOR.encrypt(plaintext.getBytes(UTF_8)));
    }

    /**
     * Converts Base64-encoded ciphertext from the DB back to plaintext.
     *
     * @param columnValue Base64 ciphertext (may be {@code null})
     * @return plaintext String or {@code null}
     */
    @Override
    public String convertToEntityAttribute(String columnValue) {
        if (columnValue == null) return null;
        byte[] cipher = Base64.getDecoder().decode(columnValue);
        return new String(EncryptorHolder.ENCRYPTOR.decrypt(cipher), UTF_8);
    }
}