module org.example.dsaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires google.maps.services;


    opens org.example.dsaproject to javafx.fxml;
    exports org.example.dsaproject;
}