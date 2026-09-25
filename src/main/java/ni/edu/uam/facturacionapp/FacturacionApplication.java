package ni.edu.uam.facturacionapp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class FacturacionApplication extends Application{
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                FacturacionApplication.class.getResource(
                        "/ni/edu/uam/facturacionapp/fxml/login-view.fxml"
                )
        );

        Scene scene = new Scene(
                fxmlLoader.load()
        );

        stage.setTitle("Facturación App");
        stage.setScene(scene);
        stage.show();
    }
}
