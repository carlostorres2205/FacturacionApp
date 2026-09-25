package ni.edu.uam.facturacionapp.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    private static final String USUARIO_CORRECTO = "Admin";
    private static final String CONTRASENA_CORRECTA = "Admin2026*";

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Label lblMensaje;


    @FXML
    private void iniciarSesion() {

        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();

        if (usuario.isEmpty()) {

            lblMensaje.setText(
                    "Debe ingresar el usuario."
            );

            txtUsuario.requestFocus();
            return;
        }

        if (contrasena.isEmpty()) {

            lblMensaje.setText(
                    "Debe ingresar la contraseña."
            );

            txtContrasena.requestFocus();
            return;
        }

        if (usuario.equals(USUARIO_CORRECTO)
                && contrasena.equals(CONTRASENA_CORRECTA)) {

            lblMensaje.setText(
                    "Inicio de sesión correcto."
            );

            abrirFormularioProducto();

        } else {

            lblMensaje.setText(
                    "Usuario o contraseña incorrectos."
            );

            txtContrasena.clear();
            txtContrasena.requestFocus();
        }
    }


    private void abrirFormularioProducto() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ni/edu/uam/facturacionapp/fxml/producto-view.fxml"
                    )
            );

            Scene scene = new Scene(
                    loader.load()
            );

            Stage ventanaProducto = new Stage();

            ventanaProducto.setTitle(
                    "Formulario Producto"
            );

            ventanaProducto.setScene(scene);

            ventanaProducto.show();


            Stage ventanaLogin =
                    (Stage) txtUsuario
                            .getScene()
                            .getWindow();

            ventanaLogin.close();

        } catch (IOException e) {

            e.printStackTrace();

            lblMensaje.setText(
                    "No se pudo abrir el formulario de producto."
            );
        }
    }
}