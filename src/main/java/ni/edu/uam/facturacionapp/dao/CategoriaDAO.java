package ni.edu.uam.facturacionapp.dao;

import ni.edu.uam.facturacionapp.model.Categoria;
import ni.edu.uam.facturacionapp.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public boolean existeNombre(String nombre)
            throws SQLException {

        String sql = """
            SELECT COUNT(*)
            FROM categoria
            WHERE LOWER(nombre) = LOWER(?)
            """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    nombre
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }
    public boolean guardar(Categoria categoria) {

        String sql = """
                INSERT INTO categoria (nombre, activa)
                VALUES (?, ?)
                """;

        try (
                Connection conexion = ConexionBD.getConnection();

                PreparedStatement ps = conexion.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {

            ps.setString(
                    1,
                    categoria.getNombre()
            );

            ps.setBoolean(
                    2,
                    categoria.isActiva()
            );

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {

                    if (rs.next()) {
                        categoria.setId(
                                rs.getInt(1)
                        );
                    }
                }

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public List<Categoria> listar() {

        List<Categoria> categorias =
                new ArrayList<>();

        String sql = """
                SELECT id, nombre, activa
                FROM categoria
                ORDER BY id
                """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                Categoria categoria =
                        new Categoria();

                categoria.setId(
                        rs.getInt("id")
                );

                categoria.setNombre(
                        rs.getString("nombre")
                );

                categoria.setActiva(
                        rs.getBoolean("activa")
                );

                categorias.add(categoria);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categorias;
    }


    public Categoria buscar(Integer id) {

        String sql = """
                SELECT id, nombre, activa
                FROM categoria
                WHERE id = ?
                """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Categoria categoria =
                            new Categoria();

                    categoria.setId(
                            rs.getInt("id")
                    );

                    categoria.setNombre(
                            rs.getString("nombre")
                    );

                    categoria.setActiva(
                            rs.getBoolean("activa")
                    );

                    return categoria;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean actualizar(Categoria categoria) throws SQLException {

        String sql = """
                UPDATE categoria
                SET nombre = ?,
                    activa = ?
                WHERE id = ?
                """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    categoria.getNombre()
            );

            ps.setBoolean(
                    2,
                    categoria.isActiva()
            );

            ps.setInt(
                    3,
                    categoria.getId()
            );

            return ps.executeUpdate() > 0;


    }}


    public boolean eliminar(Integer id) throws SQLException {

        String sql = """
                DELETE FROM categoria
                WHERE id = ?
                """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        }
    }

    public boolean tieneProductos(int categoriaId)
            throws SQLException {

        String sql = """
            SELECT COUNT(*)
            FROM producto
            WHERE categoria_id = ?
            """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    categoriaId
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean existeNombreOtro(
            String nombre,
            Integer id
    ) throws SQLException {

        String sql = """
            SELECT COUNT(*)
            FROM categoria
            WHERE LOWER(nombre) = LOWER(?)
            AND id <> ?
            """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    nombre
            );

            ps.setInt(
                    2,
                    id
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }


}
