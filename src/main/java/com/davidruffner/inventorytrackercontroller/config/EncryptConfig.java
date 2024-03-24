package com.davidruffner.inventorytrackercontroller.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptConfig {
    private final int keyLength;
    private final int iterationCount;
    private final String secretKeyFactory;
    private final String cipherInstace;
    private final String encryptionMethod;
    private final String secretKey;
    private final String salt;

    public EncryptConfig(@Value("${encrypt.keyLength}") int keyLength,
                         @Value("${encrypt.iterationCount}") int iterationCount,
                         @Value("${encrypt.secretKeyFactory}") String secretKeyFactory,
                         @Value("${encrypt.cipherInstance}") String cipherInstace,
                         @Value("${encrypt.encryptionMethod}") String encryptionMethod,
                         @Value("${encrypt.secretKey}") String secretKey,
                         @Value("${encrypt.salt}") String salt) {
        this.keyLength = keyLength;
        this.iterationCount = iterationCount;
        this.secretKeyFactory = secretKeyFactory;
        this.cipherInstace = cipherInstace;
        this.encryptionMethod = encryptionMethod;
        this.secretKey = secretKey;
        this.salt = salt;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public String getSecretKeyFactory() {
        return secretKeyFactory;
    }

    public String getCipherInstace() {
        return cipherInstace;
    }

    public String getEncryptionMethod() {
        return encryptionMethod;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getSalt() {
        return salt;
    }
}
