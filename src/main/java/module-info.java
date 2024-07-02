module com.example.clipease {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.clipease to javafx.fxml;
   //pens com.example.clipease.controller.ClipboardViewController to javafx.fxml;
    exports com.example.clipease;
    exports com.example.clipease.controller;
   // opens com.example.clipease.controller to javafx.fxml;
    //exports com.example.clipease.controller;
}