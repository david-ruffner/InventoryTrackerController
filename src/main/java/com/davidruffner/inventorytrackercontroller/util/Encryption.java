package com.davidruffner.inventorytrackercontroller.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.davidruffner.inventorytrackercontroller.config.EncryptConfig;
import com.davidruffner.inventorytrackercontroller.exceptions.AuthException;
import com.davidruffner.inventorytrackercontroller.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.USER_NOT_AUTHORIZED;


@Component
public class Encryption {
    @Autowired
    EncryptConfig encryptConfig;

    public Algorithm getJWTAlgorithm() {
        return Algorithm.HMAC256(encryptConfig.getSecretKey());
    }

    public DecodedJWT getJWTFromToken(String token) throws AuthException, BadRequestException {
        String decryptedToken = "";
        try {
            decryptedToken = decryptFromAES(token);
        } catch (RuntimeException ex) {
            throw new BadRequestException.Builder(this.getClass(),
                    "Given token is invalid").build();
        }

        try {
            return JWT.decode(decryptedToken);
        } catch (JWTDecodeException ex) {
            throw new AuthException.Builder(USER_NOT_AUTHORIZED, this.getClass())
                    .setMessage("Given token is invalid")
                    .build();
        } catch (Exception ex) {
            throw new BadRequestException.Builder(this.getClass(),
                    "Given token is invalid").build();
        }
    }

    public String decryptFromAES(String encryptedMsg) throws RuntimeException {
        try {
            byte[] encryptedData = Base64.getDecoder().decode(encryptedMsg);
            byte[] iv = new byte[16];
            System.arraycopy(encryptedData, 0, iv, 0, iv.length);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(encryptConfig.getSecretKeyFactory());
            KeySpec spec = new PBEKeySpec(encryptConfig.getSecretKey().toCharArray(), encryptConfig.getSalt().getBytes(),
                    encryptConfig.getIterationCount(), encryptConfig.getKeyLength());
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), encryptConfig.getEncryptionMethod());

            Cipher cipher = Cipher.getInstance(encryptConfig.getCipherInstace());
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);

            byte[] cipherText = new byte[encryptedData.length - 16];
            System.arraycopy(encryptedData, 16, cipherText, 0, cipherText.length);

            byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText, "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public String encryptToAES(String msg) throws RuntimeException {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(encryptConfig.getSecretKeyFactory());
            KeySpec spec = new PBEKeySpec(encryptConfig.getSecretKey().toCharArray(),
                    encryptConfig.getSalt().getBytes(), encryptConfig.getIterationCount(),
                    encryptConfig.getKeyLength());
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(),
                    encryptConfig.getEncryptionMethod());

            Cipher cipher = Cipher.getInstance(encryptConfig.getCipherInstace());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

            byte[] cipherText = cipher.doFinal(msg.getBytes("UTF-8"));
            byte[] encryptedData = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
