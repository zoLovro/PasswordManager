package com.roburo.passwordmanager;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PasswordsController {
    @FXML
    private ListView<String> credentialsList;

    @FXML
    public void initialize() {
        try {
            String currentUser = Session.getCurrentUsername(); // e.g., set after login
            Path path = Paths.get(System.getProperty("user.home"), ".passwordmanager", currentUser + ".enc");
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                List<String> decryptedEntries = lines.stream()
                        .map(line -> {
                            String[] parts = line.split(" : ");
                            if (parts.length != 2) return line;
                            String decrypted = EncryptionUtils.decrypt(parts[1], Session.getEncryptionKey());
                            System.out.println((parts[1]));
                            return parts[0] + " : " + (decrypted != null ? decrypted : "Decryption Error");
                        })
                        .toList();


                credentialsList.getItems().addAll(decryptedEntries);
            }
        } catch (IOException e) {
            credentialsList.getItems().add("Failed to read saved credentials.");
        }
    }

    public class Session {
        private static String currentUsername;
        private static String encryptionKey;

        public static void setCurrentUsername(String username) {
            currentUsername = username;
        }

        public static void setEncryptionKey(String key) {
            encryptionKey = key;
        }

        public static String getCurrentUsername() {
            return currentUsername;
        }

        public static String getEncryptionKey() {
            return encryptionKey;
        }
    }

    @FXML
    private void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) credentialsList.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Password Manager");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
