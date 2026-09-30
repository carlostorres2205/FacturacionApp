package ni.edu.uam.facturacionapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL =
            "jdbc:postgresql://localhost:5432/tienda_javafx";

    private static final String USUARIO = "postgres";

    private static final String PASSWORD = "1234"; //Esto lo tenes que cambiar a la contraseña que tenes vos.

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
