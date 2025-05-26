module com.projektjava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.projektjava to javafx.fxml;
    exports com.projektjava;
}