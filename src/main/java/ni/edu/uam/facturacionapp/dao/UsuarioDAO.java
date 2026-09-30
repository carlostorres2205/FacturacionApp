package ni.edu.uam.facturacionapp.dao;

import ni.edu.uam.facturacionapp.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Integer validarUsuario(
            String usuario,
            String contrasena
    ) {

        String sql = """
                SELECT id
                FROM usuario
                WHERE nombre_usuario = ?
                AND contrasena = ?
                AND activo = true
                """;

        try (
                Connection connection =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {

            ps.setString(1, usuario);
            ps.setString(2, contrasena);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean registrarInicioSesion(
            Integer usuarioId
    ) {

        String sql = """
                INSERT INTO inicio_sesion
                (
                    usuario_id
                )
                VALUES (?)
                """;

        try (
                Connection connection =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    usuarioId
            );

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }
}