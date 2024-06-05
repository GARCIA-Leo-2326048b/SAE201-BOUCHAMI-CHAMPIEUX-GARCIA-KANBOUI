module com.example.sae201bouchamichampieuxgarciakanboui {
    requires javafx.controls;
    requires javafx.fxml;
    requires junit;


    opens com.example.sae201bouchamichampieuxgarciakanboui to javafx.fxml;
    exports com.example.sae201bouchamichampieuxgarciakanboui;
}