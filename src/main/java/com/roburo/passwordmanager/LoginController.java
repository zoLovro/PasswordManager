package com.roburo.passwordmanager;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    protected void onLoginClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Username and password cannot be empty.");
            return;
        }

        String hashedInput = hashPassword(password);
        Path path = Paths.get(System.getProperty("user.home"), "users.txt");

        try {
            for (String line : Files.readAllLines(path)) {
                String[] parts = line.split(":");
                if (parts.length != 2) continue;
                if (parts[0].equals(username) && parts[1].equals(hashedInput)) {
                    // store RAW password + username in Session
                    Session.setCurrentUsername(username);
                    Session.setEncryptionKey( deriveAESKey(password) );

                    loadMainScreen();      // <— no args now
                    return;
                }
            }
            showAlert("Invalid credentials.");
        } catch (IOException e) {
            showAlert("Could not read users file.");
        }
    }

    @FXML
    protected void onRegisterClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("registerView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Password Manager - Register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainView.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Password Manager");
        } catch (IOException e) {
            showAlert("Error loading main screen.");
        }
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static String deriveAESKey(String masterPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(masterPassword.getBytes(StandardCharsets.UTF_8));
            return new String(Arrays.copyOf(hash, 16), StandardCharsets.ISO_8859_1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
