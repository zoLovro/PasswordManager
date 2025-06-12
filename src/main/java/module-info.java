module com.roburo.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.roburo.passwordmanager to javafx.fxml;
    exports com.roburo.passwordmanager;
}