package com.roburo.passwordmanager;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class Controller {
    @FXML private TextField passwordField;
    @FXML private TextField lengthField;

    private final PasswordManager passwordManager = new PasswordManager();

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
    protected void onSaveClick() {
        // TODO: implement save logic if desired
        System.out.println("Save clicked (not implemented)");
    }
}