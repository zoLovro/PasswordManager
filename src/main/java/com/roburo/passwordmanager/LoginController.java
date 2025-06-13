package com.roburo.passwordmanager;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.security.MessageDigest;

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
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length != 2) continue;
                String fileUser = parts[0];
                String fileHash = parts[1];

                if (fileUser.equals(username) && fileHash.equals(hashedInput)) {
                    loadMainScreen(username, password); // pass raw password, NOT hashedInput
                    PasswordsController.Session.setEncryptionKey(deriveAESKey(password));
                    PasswordsController.Session.setCurrentUsername(username);

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

            // Get the current stage
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("Password Manager - Register");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMainScreen(String username, String hashedInput) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainView.fxml"));
            Scene scene = new Scene(loader.load());

            // Pass username and key to the controller
            ApplicationController controller = loader.getController();
            controller.setUserCredentials(username, hashedInput);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert("Error loading main screen.");
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static String deriveAESKey(String masterPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(masterPassword.getBytes(StandardCharsets.UTF_8));
            // Convert first 16 bytes to a hex string (or use raw bytes in SecretKeySpec)
            byte[] keyBytes = Arrays.copyOf(hash, 16);
            return new String(keyBytes, StandardCharsets.ISO_8859_1); // Using ISO_8859_1 to keep bytes unchanged
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
