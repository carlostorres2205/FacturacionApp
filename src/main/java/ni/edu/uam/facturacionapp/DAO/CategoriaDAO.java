package ni.edu.uam.facturacionapp.DAO;

import ni.edu.uam.facturacionapp.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    private final Connection conexion;

    public CategoriaDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public boolean guardar(Categoria categoria) {

        String sql = """
                INSERT INTO categoria (nombre, activa)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {

            ps.setString(1, categoria.getNombre());
            ps.setBoolean(2, categoria.isActiva());

            int filas = ps.executeUpdate();

            if (filas > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {

                    if (rs.next()) {
                        categoria.setId(rs.getInt(1));
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

        List<Categoria> categorias = new ArrayList<>();

        String sql = """
                SELECT id, nombre, activa
                FROM categoria
                ORDER BY id
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Categoria categoria = new Categoria();

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

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Categoria categoria = new Categoria();

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

    public boolean actualizar(Categoria categoria) {

        String sql = """
                UPDATE categoria
                SET nombre = ?,
                    activa = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

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

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean eliminar(Integer id) {

        String sql = """
                DELETE FROM categoria
                WHERE id = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
