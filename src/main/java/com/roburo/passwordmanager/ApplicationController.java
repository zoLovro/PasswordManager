package com.roburo.passwordmanager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import static com.roburo.passwordmanager.LoginController.deriveAESKey;

public class ApplicationController {
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private TextField lengthField;
    private String currentUsername;
    private String encryptionKey;

    private final PasswordLogic passwordManager = new PasswordLogic();

    public void setUserCredentials(String username, String rawPassword) {
        this.currentUsername = username;
        this.encryptionKey = deriveAESKey(rawPassword);
    }

    @FXML
    protected void onGenerateClick() {
        int length;
        try {
            length = Integer.parseInt(lengthField.getText());
        } catch (NumberFormatException e) {
            length = 12; // fallback
        }

        String password = passwordManager.generatePassword(length);
        passwordField.setText(password);
    }

    @FXML
    protected void onCopyClick() {
        String password = passwordField.getText();
        if (password == null || password.isEmpty()) return;

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(password);
        clipboard.setContent(content);
    }


    @FXML
    protected void onSaveClick() throws IOException {
        if (encryptionKey == null || currentUsername == null) {
            showError("User not authenticated.");
            return;
        }

        String password = passwordField.getText();
        if (password == null || password.isEmpty()) return;

        String encryptedPassword = EncryptionUtils.encrypt(password, encryptionKey);
        String serviceUsername = usernameField.getText();
        if (serviceUsername == null || serviceUsername.isEmpty()) return;

        Path path = Paths.get(System.getProperty("user.home"), ".passwordmanager", currentUsername + ".enc");
        Files.createDirectories(path.getParent());
        String line = serviceUsername + " : " + encryptedPassword;

        try {
            Files.write(path, List.of(line), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            showInfo("Credentials saved!");
        } catch (IOException e) {
            showError("Error saving credentials: " + e.getMessage());
        }
    }
    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    @FXML
    protected void onPasswordsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("passwordsView.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("Password Manager - Passwords");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String deriveAESKey(String masterPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(masterPassword.getBytes(StandardCharsets.UTF_8));
            byte[] keyBytes = Arrays.copyOf(hash, 16);
            return new String(keyBytes, StandardCharsets.ISO_8859_1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}