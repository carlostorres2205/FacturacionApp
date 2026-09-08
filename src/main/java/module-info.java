module ni.edu.uam.facturacionapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    exports ni.edu.uam.facturacionapp;
    exports ni.edu.uam.facturacionapp.model;

    opens ni.edu.uam.facturacionapp to javafx.fxml;
    opens ni.edu.uam.facturacionapp.Controller to javafx.fxml;
    opens ni.edu.uam.facturacionapp.model to javafx.base;
}