package ni.edu.uam.facturacionapp.DAO;

import ni.edu.uam.facturacionapp.model.Categoria;
import ni.edu.uam.facturacionapp.model.Producto;
import ni.edu.uam.facturacionapp.util.conexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    public boolean guardar(Producto producto) {

        String sql = """
                INSERT INTO producto
                (
                    codigo,
                    nombre,
                    categoria_id,
                    precio_venta,
                    existencia,
                    ruta_imagen,
                    activo
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conexion =
                        conexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            ps.setString(
                    1,
                    producto.getCodigo()
            );

            ps.setString(
                    2,
                    producto.getNombre()
            );

            ps.setInt(
                    3,
                    producto.getCategoria().getId()
            );

            ps.setBigDecimal(
                    4,
                    producto.getPrecioVenta()
            );

            ps.setInt(
                    5,
                    producto.getExistencia()
            );

            ps.setString(
                    6,
                    producto.getRutaImagen()
            );

            ps.setBoolean(
                    7,
                    producto.isActivo()
            );

            int filasAfectadas =
                    ps.executeUpdate();

            if (filasAfectadas > 0) {

                try (ResultSet rs =
                             ps.getGeneratedKeys()) {

                    if (rs.next()) {

                        producto.setId(
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


    public List<Producto> listar() {

        List<Producto> productos =
                new ArrayList<>();

        String sql = """
                SELECT
                    p.id,
                    p.codigo,
                    p.nombre,
                    p.precio_venta,
                    p.existencia,
                    p.ruta_imagen,
                    p.activo,
                    c.id AS categoria_id,
                    c.nombre AS categoria_nombre,
                    c.activa AS categoria_activa
                FROM producto p
                INNER JOIN categoria c
                    ON p.categoria_id = c.id
                ORDER BY p.id
                """;

        try (
                Connection conexion =
                        conexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                Producto producto =
                        construirProducto(rs);

                productos.add(producto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }


    public Producto buscar(Integer id) {

        String sql = """
                SELECT
                    p.id,
                    p.codigo,
                    p.nombre,
                    p.precio_venta,
                    p.existencia,
                    p.ruta_imagen,
                    p.activo,
                    c.id AS categoria_id,
                    c.nombre AS categoria_nombre,
                    c.activa AS categoria_activa
                FROM producto p
                INNER JOIN categoria c
                    ON p.categoria_id = c.id
                WHERE p.id = ?
                """;

        try (
                Connection conexion =
                        conexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return construirProducto(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean actualizar(Producto producto) {

        String sql = """
                UPDATE producto
                SET codigo = ?,
                    nombre = ?,
                    categoria_id = ?,
                    precio_venta = ?,
                    existencia = ?,
                    ruta_imagen = ?,
                    activo = ?
                WHERE id = ?
                """;

        try (
                Connection conexion =
                        conexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    producto.getCodigo()
            );

            ps.setString(
                    2,
                    producto.getNombre()
            );

            ps.setInt(
                    3,
                    producto.getCategoria().getId()
            );

            ps.setBigDecimal(
                    4,
                    producto.getPrecioVenta()
            );

            ps.setInt(
                    5,
                    producto.getExistencia()
            );

            ps.setString(
                    6,
                    producto.getRutaImagen()
            );

            ps.setBoolean(
                    7,
                    producto.isActivo()
            );

            ps.setInt(
                    8,
                    producto.getId()
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean eliminar(Integer id) {

        String sql = """
                DELETE FROM producto
                WHERE id = ?
                """;

        try (
                Connection conexion =
                        conexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    private Producto construirProducto(
            ResultSet rs
    ) throws SQLException {

        Categoria categoria =
                new Categoria();

        categoria.setId(
                rs.getInt("categoria_id")
        );

        categoria.setNombre(
                rs.getString("categoria_nombre")
        );

        categoria.setActiva(
                rs.getBoolean("categoria_activa")
        );


        Producto producto =
                new Producto();

        producto.setId(
                rs.getInt("id")
        );

        producto.setCodigo(
                rs.getString("codigo")
        );

        producto.setNombre(
                rs.getString("nombre")
        );

        producto.setCategoria(
                categoria
        );

        producto.setPrecioVenta(
                rs.getBigDecimal("precio_venta")
        );

        producto.setExistencia(
                rs.getInt("existencia")
        );

        producto.setRutaImagen(
                rs.getString("ruta_imagen")
        );

        producto.setActivo(
                rs.getBoolean("activo")
        );

        return producto;
    }
}
