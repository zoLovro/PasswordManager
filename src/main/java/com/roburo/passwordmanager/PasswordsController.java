package com.roburo.passwordmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class PasswordsController {
    @FXML private TableView<Credential> credentialsTable;
    @FXML private TableColumn<Credential,String> siteColumn;
    @FXML private TableColumn<Credential,String> userColumn;
    @FXML private TableColumn<Credential,String> passColumn;
    @FXML private TableColumn<Credential,Void> actionColumn;

    @FXML
    public void initialize() {
        // 1. Wire model properties
        siteColumn.setCellValueFactory(new PropertyValueFactory<>("site"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        // 2. Add per-row “Copy” button
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button copy = new Button("Copy");
            {
                copy.setOnAction(evt -> {
                    Credential c = getTableView().getItems().get(getIndex());
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString("Site: " + c.getSite() +
                            "\nUser: " + c.getUsername() +
                            "\nPass: " + c.getPassword());
                    Clipboard.getSystemClipboard().setContent(cc);
                });
                copy.setStyle("-fx-background-radius:8; -fx-padding:4 8;");
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : copy);
            }
        });

        loadCredentials();
    }

    private void loadCredentials() {
        String user = Session.getCurrentUsername();
        String key  = Session.getEncryptionKey();
        System.out.println("Session user=" + user + ", keySet=" + (key != null));

        if (user == null || key == null) return;

        Path file = Paths.get(System.getProperty("user.home"),
                ".passwordmanager", user + ".enc");
        System.out.println("Looking for file: " + file);
        if (!Files.exists(file)) {
            System.out.println("No credentials file found");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(file);
            System.out.println("Raw lines: " + lines);
            ObservableList<Credential> data = FXCollections.observableArrayList();

            for (String line : lines) {
                // split on colon with optional spaces
                String[] parts = line.split("\\s*:\\s*");
                if (parts.length != 3) {
                    System.out.println("Skipping malformed entry: " + line);
                    continue;
                }
                String site = parts[0];
                String svcUser = parts[1];
                String decrypted = EncryptionUtils.decrypt(parts[2], key);
                data.add(new Credential(site, svcUser, decrypted == null ? "" : decrypted));
            }

            System.out.println("Loaded credentials count: " + data.size());
            credentialsTable.setItems(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainView.fxml"));
            Stage stage = (Stage) credentialsTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Password Manager");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
