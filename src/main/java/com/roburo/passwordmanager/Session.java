package com.roburo.passwordmanager;

public class Session {
    private static String currentUsername;
    private static String encryptionKey;

    public static void setCurrentUsername(String u) {
        currentUsername = u;
    }
    public static void setEncryptionKey(String k) {
        encryptionKey = k;
    }
    public static String getCurrentUsername() {
        return currentUsername;
    }
    public static String getEncryptionKey() {
        return encryptionKey;
    }
}
