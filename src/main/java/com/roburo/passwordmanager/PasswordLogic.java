package com.roburo.passwordmanager;

import java.security.SecureRandom;

public class PasswordLogic {
    // Generating the password
    private static final String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.?/";
    private static final String ALL_CHARACTERS = CAPITAL_LETTERS + LOWERCASE_LETTERS + NUMBERS + SYMBOLS;
    private static final SecureRandom random = new SecureRandom();


    public String generatePassword(int length) {
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALL_CHARACTERS.length());
            password.append(ALL_CHARACTERS.charAt(index));
        }
        int passwordLength = password.length();

        if(!containsSymbol(password.toString())) {
            int index = random.nextInt(passwordLength);
            char symbol = SYMBOLS.charAt(random.nextInt(SYMBOLS.length()));
            password.setCharAt(index, symbol);
        }
        if(!containsNumber(password.toString())) {
            int index = random.nextInt(passwordLength);
            char symbol = NUMBERS.charAt(random.nextInt(NUMBERS.length()));
            password.setCharAt(index, symbol);
        }
        if(!containsCapitalLetter(password.toString())) {
            int index = random.nextInt(passwordLength);
            char symbol = CAPITAL_LETTERS.charAt(random.nextInt(CAPITAL_LETTERS.length()));
            password.setCharAt(index, symbol);
        }
        if(!containsLowercaseLetter(password.toString())) {
            int index = random.nextInt(passwordLength);
            char symbol = LOWERCASE_LETTERS.charAt(random.nextInt(LOWERCASE_LETTERS.length()));
            password.setCharAt(index, symbol);
        }

        return password.toString();
    }

    public boolean containsSymbol(String password) {
        for(char c : password.toCharArray()) {
            if(SYMBOLS.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }
    public boolean containsNumber(String password) {
        for(char c : password.toCharArray()) {
            if(NUMBERS.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }
    public boolean containsCapitalLetter(String password) {
        for(char c : password.toCharArray()) {
            if(CAPITAL_LETTERS.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }
    public boolean containsLowercaseLetter(String password) {
        for(char c : password.toCharArray()) {
            if(LOWERCASE_LETTERS.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        PasswordLogic pm = new PasswordLogic();
        System.out.println(pm.generatePassword(12));
    }
}
