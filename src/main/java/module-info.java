module com.example.clipease {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.clipease to javafx.fxml;
    exports com.example.clipease;
}