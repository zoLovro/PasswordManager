package com.roburo.passwordmanager;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class EncryptionUtils {
    public static String encrypt(String value, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec("abcdefghijklmnop".getBytes()); // Use random IV for better security in real apps
            SecretKeySpec skeySpec = getKey(key);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            System.err.println("Encryption error: " + ex.getMessage());
            return null;
        }
    }

    public static String decrypt(String encrypted, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec("abcdefghijklmnop".getBytes());
            SecretKeySpec skeySpec = getKey(key);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception ex) {
            System.err.println("Decryption error: " + ex.getMessage());
            return null;
        }
    }

    public static SecretKeySpec getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        keyBytes = Arrays.copyOf(keyBytes, 16); // use first 16 bytes only (pad or trim)
        return new SecretKeySpec(keyBytes, "AES");
    }
}
