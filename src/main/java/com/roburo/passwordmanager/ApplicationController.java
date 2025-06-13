package com.roburo.passwordmanager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

public class ApplicationController {
    @FXML private TextField siteField;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private TextField lengthField;

    private final PasswordLogic passwordManager = new PasswordLogic();

    /** Remove setUserCredentials entirely—Session holds those now */

    @FXML
    protected void onGenerateClick() {
        int length = 12;
        try { length = Integer.parseInt(lengthField.getText()); }
        catch (NumberFormatException ignored) {}
        passwordField.setText(passwordManager.generatePassword(length));
    }

    @FXML
    protected void onCopyClick() {
        String pwd = passwordField.getText();
        if (pwd == null || pwd.isEmpty()) return;
        ClipboardContent cc = new ClipboardContent();
        cc.putString(pwd);
        Clipboard.getSystemClipboard().setContent(cc);
    }

    @FXML
    protected void onSaveClick() throws IOException {
        String currentUser = Session.getCurrentUsername();
        String key = Session.getEncryptionKey();
        if (currentUser == null || key == null) {
            showError("User not authenticated.");
            return;
        }

        String site = siteField.getText();
        String serviceUser = usernameField.getText();
        String pwd = passwordField.getText();
        if (site.isEmpty() || serviceUser.isEmpty() || pwd.isEmpty()) return;

        String encrypted = EncryptionUtils.encrypt(pwd, key);
        Path dir = Paths.get(System.getProperty("user.home"), ".passwordmanager");
        Files.createDirectories(dir);
        Path file = dir.resolve(currentUser + ".enc");

        String line = site + " : " + serviceUser + " : " + encrypted;
        Files.write(file, List.of(line),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        showInfo("Saved!");
    }

    @FXML
    protected void onPasswordsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("passwordsView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) siteField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Passwords");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showInfo(String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }
    private void showError(String m) {
        new Alert(Alert.AlertType.ERROR, m).showAndWait();
    }
}
