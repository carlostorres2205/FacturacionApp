package ni.edu.uam.facturacionapp.dao;

import ni.edu.uam.facturacionapp.model.Categoria;
import ni.edu.uam.facturacionapp.model.Producto;
import ni.edu.uam.facturacionapp.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {


    public boolean guardar(Producto producto)
            throws SQLException {

        String sql = """
                INSERT INTO producto
                (
                    codigo,
                    nombre,
                    categoria_id,
                    precio_venta,
                    existencia,
                    activo
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

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

            ps.setBoolean(
                    6,
                    producto.isActivo()
            );


            int filasAfectadas =
                    ps.executeUpdate();


            if (filasAfectadas > 0) {

                try (
                        ResultSet rs =
                                ps.getGeneratedKeys()
                ) {

                    if (rs.next()) {

                        producto.setId(
                                rs.getInt(1)
                        );
                    }
                }

                return true;
            }
        }

        return false;
    }


    public List<Producto> listar()
            throws SQLException {

        List<Producto> productos =
                new ArrayList<>();


        String sql = """
                SELECT
                    p.id,
                    p.codigo,
                    p.nombre,
                    p.precio_venta,
                    p.existencia,
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

                producto.setActivo(
                        rs.getBoolean("activo")
                );


                productos.add(
                        producto
                );
            }
        }

        return productos;
    }


    public Producto buscar(Integer id)
            throws SQLException {

        String sql = """
                SELECT
                    p.id,
                    p.codigo,
                    p.nombre,
                    p.precio_venta,
                    p.existencia,
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
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    id
            );


            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

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

                    producto.setActivo(
                            rs.getBoolean("activo")
                    );


                    return producto;
                }
            }
        }

        return null;
    }


    public boolean actualizar(Producto producto)
            throws SQLException {

        String sql = """
                UPDATE producto
                SET
                    codigo = ?,
                    nombre = ?,
                    categoria_id = ?,
                    precio_venta = ?,
                    existencia = ?,
                    activo = ?
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

            ps.setBoolean(
                    6,
                    producto.isActivo()
            );

            ps.setInt(
                    7,
                    producto.getId()
            );


            return ps.executeUpdate() > 0;
        }
    }


    public boolean eliminar(Integer id)
            throws SQLException {

        String sql = """
                DELETE FROM producto
                WHERE id = ?
                """;


        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    id
            );

            return ps.executeUpdate() > 0;
        }
    }


    public boolean existeCodigo(String codigo)
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM producto
                WHERE codigo = ?
                """;


        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    codigo
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


    public boolean existeCodigo(
            String codigo,
            Integer idExcluir
    ) throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM producto
                WHERE codigo = ?
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
                    codigo
            );

            ps.setInt(
                    2,
                    idExcluir
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