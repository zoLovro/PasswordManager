module com.roburo.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;


    opens com.roburo.passwordmanager to javafx.fxml;
    exports com.roburo.passwordmanager;
}