package ni.edu.uam.facturacionapp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.facturacionapp.dao.CategoriaDAO;
import ni.edu.uam.facturacionapp.dao.ProductoDAO;
import ni.edu.uam.facturacionapp.model.Categoria;
import ni.edu.uam.facturacionapp.model.Producto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ProductoController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private ComboBox<Categoria> cbCategoria;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtExistencia;

    @FXML
    private CheckBox chkActivo;

    @FXML
    private Button btnNuevaCategoria;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnLimpiar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Label lblEstado;

    @FXML
    private TableView<Producto> tvProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, BigDecimal> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colExistencia;

    @FXML
    private TableColumn<Producto, Boolean> colActivo;

    @FXML
    private TextField txtBuscar;

    @FXML
    private ComboBox<String> cbFiltroEstado;

    @FXML
    private ComboBox<String> cbFiltroCategoria;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    private final ObservableList<Producto> productos =
            FXCollections.observableArrayList();

    private FilteredList<Producto> productosFiltrados;

    @FXML
    public void initialize() {

        cargarCategorias();

        configurarTabla();

        productosFiltrados =
                new FilteredList<>(
                        productos,
                        producto -> true
                );

        tvProductos.setItems(
                productosFiltrados
        );

        configurarFiltros();

        cargarProductos();

        chkActivo.setSelected(true);
    }

    @FXML
    private void guardarProducto() {

        try {

            Producto producto =
                    obtenerProductoFormulario();

            if (productoDAO.existeCodigo(
                    producto.getCodigo()
            )) {

                mostrarAdvertencia(
                        "Ya existe un producto con ese código."
                );

                txtCodigo.requestFocus();

                return;
            }

            boolean guardado =
                    productoDAO.guardar(
                            producto
                    );

            if (guardado) {

                mostrarExito(
                        "Producto registrado",
                        "El producto se guardó correctamente."
                );

                cargarProductos();

                limpiarFormulario();

            } else {

                mostrarError(
                        "No fue posible registrar el producto."
                );
            }

        } catch (IllegalArgumentException e) {

            mostrarAdvertencia(
                    e.getMessage()
            );

        } catch (SQLException e) {

            mostrarError(
                    "No fue posible registrar el producto."
            );

            System.err.println(
                    "Error al guardar producto: "
                            + e.getMessage()
            );
        }
    }

    private Producto obtenerProductoFormulario() {

        String codigo =
                txtCodigo
                        .getText()
                        .trim();

        String nombre =
                txtNombre
                        .getText()
                        .trim();

        if (codigo.isEmpty()) {

            txtCodigo.requestFocus();

            throw new IllegalArgumentException(
                    "El código es obligatorio."
            );
        }

        if (nombre.isEmpty()) {

            txtNombre.requestFocus();

            throw new IllegalArgumentException(
                    "El nombre es obligatorio."
            );
        }

        Categoria categoria =
                cbCategoria
                        .getSelectionModel()
                        .getSelectedItem();

        if (categoria == null) {

            cbCategoria.requestFocus();

            throw new IllegalArgumentException(
                    "Debe seleccionar una categoría."
            );
        }

        BigDecimal precio;

        try {

            precio =
                    new BigDecimal(
                            txtPrecio
                                    .getText()
                                    .trim()
                    );

        } catch (NumberFormatException e) {

            txtPrecio.requestFocus();

            throw new IllegalArgumentException(
                    "El precio debe ser un valor numérico."
            );
        }

        if (precio.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            txtPrecio.requestFocus();

            throw new IllegalArgumentException(
                    "El precio de venta debe ser mayor que cero."
            );
        }

        int existencia;

        try {

            existencia =
                    Integer.parseInt(
                            txtExistencia
                                    .getText()
                                    .trim()
                    );

        } catch (NumberFormatException e) {

            txtExistencia.requestFocus();

            throw new IllegalArgumentException(
                    "La existencia debe ser un número entero."
            );
        }

        if (existencia < 0) {

            txtExistencia.requestFocus();

            throw new IllegalArgumentException(
                    "La existencia no puede ser negativa."
            );
        }

        Producto producto =
                new Producto();

        producto.setCodigo(
                codigo
        );

        producto.setNombre(
                nombre
        );

        producto.setCategoria(
                categoria
        );

        producto.setPrecioVenta(
                precio
        );

        producto.setExistencia(
                existencia
        );

        producto.setActivo(
                chkActivo.isSelected()
        );

        return producto;
    }

    @FXML
    private void eliminarProducto() {

        Producto seleccionado =
                tvProductos
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarAdvertencia(
                    "Debe seleccionar un producto para eliminar."
            );

            return;
        }

        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmacion.setTitle(
                "Eliminar producto"
        );

        confirmacion.setHeaderText(
                "¿Desea eliminar este producto?"
        );

        confirmacion.setContentText(
                seleccionado.getCodigo()
                        + " - "
                        + seleccionado.getNombre()
        );

        Optional<ButtonType> respuesta =
                confirmacion.showAndWait();

        if (respuesta.isEmpty()
                || respuesta.get() != ButtonType.OK) {

            return;
        }

        try {

            boolean eliminado =
                    productoDAO.eliminar(
                            seleccionado.getId()
                    );

            if (eliminado) {

                cargarProductos();

                tvProductos
                        .getSelectionModel()
                        .clearSelection();

                lblEstado.setText(
                        "Producto eliminado correctamente."
                );

            } else {

                mostrarError(
                        "No fue posible eliminar el producto."
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "No fue posible eliminar el producto."
            );

            System.err.println(
                    "Error al eliminar producto: "
                            + e.getMessage()
            );
        }
    }

    private void cargarCategorias() {

        try {

            List<Categoria> categorias =
                    categoriaDAO.listar();

            cbCategoria
                    .getItems()
                    .clear();

            cbCategoria
                    .getItems()
                    .addAll(
                            categorias
                    );

            if (categorias.isEmpty()) {

                lblEstado.setText(
                        "No hay categorías registradas."
                );

            } else {

                lblEstado.setText(
                        "Categorías cargadas correctamente."
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "No fue posible cargar las categorías."
            );

            System.err.println(
                    "Error al cargar categorías: "
                            + e.getMessage()
            );
        }
    }

    @FXML
    private void agregarCategoria() {

        TextInputDialog dialogo =
                new TextInputDialog();

        dialogo.setTitle(
                "Nueva categoría"
        );

        dialogo.setHeaderText(
                "Registrar una nueva categoría"
        );

        dialogo.setContentText(
                "Nombre:"
        );

        Optional<String> resultado =
                dialogo.showAndWait();

        if (resultado.isEmpty()) {

            return;
        }

        String nombre =
                resultado
                        .get()
                        .trim();

        if (nombre.isEmpty()) {

            mostrarAdvertencia(
                    "El nombre de la categoría es obligatorio."
            );

            return;
        }

        try {

            if (categoriaDAO.existeNombre(
                    nombre
            )) {

                mostrarAdvertencia(
                        "Ya existe una categoría con ese nombre."
                );

                seleccionarCategoriaPorNombre(
                        nombre
                );

                return;
            }

            Categoria nuevaCategoria =
                    new Categoria();

            nuevaCategoria.setNombre(
                    nombre
            );

            nuevaCategoria.setActiva(
                    true
            );

            boolean guardada =
                    categoriaDAO.guardar(
                            nuevaCategoria
                    );

            if (guardada) {

                cargarCategorias();

                seleccionarCategoriaPorId(
                        nuevaCategoria.getId()
                );

                actualizarCategoriasFiltro();

                lblEstado.setText(
                        "Categoría agregada correctamente."
                );

            } else {

                mostrarError(
                        "No fue posible registrar la categoría."
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "No fue posible registrar la categoría."
            );

            System.err.println(
                    "Error al guardar categoría: "
                            + e.getMessage()
            );
        }
    }

    private void seleccionarCategoriaPorNombre(
            String nombre
    ) {

        for (Categoria categoria :
                cbCategoria.getItems()) {

            if (categoria
                    .getNombre()
                    .equalsIgnoreCase(
                            nombre
                    )) {

                cbCategoria.setValue(
                        categoria
                );

                return;
            }
        }
    }

    private void seleccionarCategoriaPorId(
            Integer id
    ) {

        if (id == null) {

            return;
        }

        for (Categoria categoria :
                cbCategoria.getItems()) {

            if (categoria.getId() != null
                    && categoria
                    .getId()
                    .equals(id)) {

                cbCategoria.setValue(
                        categoria
                );

                return;
            }
        }
    }

    private void cargarProductos() {

        try {

            productos.clear();

            productos.addAll(
                    productoDAO.listar()
            );

        } catch (SQLException e) {

            mostrarError(
                    "No fue posible cargar los productos."
            );

            System.err.println(
                    "Error al cargar productos: "
                            + e.getMessage()
            );
        }
    }

    private void configurarTabla() {

        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>(
                        "codigo"
                )
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>(
                        "nombre"
                )
        );

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>(
                        "precioVenta"
                )
        );

        colExistencia.setCellValueFactory(
                new PropertyValueFactory<>(
                        "existencia"
                )
        );

        colActivo.setCellValueFactory(
                new PropertyValueFactory<>(
                        "activo"
                )
        );

        colCategoria.setCellValueFactory(
                datos -> {

                    Producto producto =
                            datos.getValue();

                    if (producto.getCategoria() == null) {

                        return new javafx.beans.property.SimpleStringProperty(
                                ""
                        );
                    }

                    return new javafx.beans.property.SimpleStringProperty(
                            producto
                                    .getCategoria()
                                    .getNombre()
                    );
                }
        );
    }

    @FXML
    private void limpiarFormulario() {

        txtCodigo.clear();

        txtNombre.clear();

        cbCategoria.setValue(
                null
        );

        txtPrecio.clear();

        txtExistencia.clear();

        chkActivo.setSelected(
                true
        );

        tvProductos
                .getSelectionModel()
                .clearSelection();

        lblEstado.setText(
                "Formulario limpiado."
        );

        txtCodigo.requestFocus();
    }

    @FXML
    private void buscarProducto() {

        aplicarFiltros();
    }

    @FXML
    private void limpiarBusqueda() {

        txtBuscar.clear();

        aplicarFiltros();

        txtBuscar.requestFocus();
    }

    @FXML
    private void limpiarFiltros() {

        txtBuscar.clear();

        cbFiltroEstado.setValue(
                "Todos"
        );

        cbFiltroCategoria.setValue(
                "Todas las categorías"
        );

        aplicarFiltros();

        lblEstado.setText(
                "Mostrando todos los productos."
        );
    }

    private void configurarFiltros() {

        cbFiltroEstado
                .getItems()
                .clear();

        cbFiltroEstado
                .getItems()
                .addAll(
                        "Todos",
                        "Activos",
                        "Inactivos"
                );

        cbFiltroEstado.setValue(
                "Todos"
        );

        actualizarCategoriasFiltro();

        cbFiltroEstado.setOnAction(
                event -> aplicarFiltros()
        );

        cbFiltroCategoria.setOnAction(
                event -> aplicarFiltros()
        );
    }

    private void actualizarCategoriasFiltro() {

        String seleccionActual =
                cbFiltroCategoria
                        .getValue();

        cbFiltroCategoria
                .getItems()
                .clear();

        cbFiltroCategoria
                .getItems()
                .add(
                        "Todas las categorías"
                );

        try {

            List<Categoria> categorias =
                    categoriaDAO.listar();

            for (Categoria categoria :
                    categorias) {

                cbFiltroCategoria
                        .getItems()
                        .add(
                                categoria.getNombre()
                        );
            }

            if (seleccionActual != null
                    && cbFiltroCategoria
                    .getItems()
                    .contains(
                            seleccionActual
                    )) {

                cbFiltroCategoria.setValue(
                        seleccionActual
                );

            } else {

                cbFiltroCategoria.setValue(
                        "Todas las categorías"
                );
            }

        } catch (SQLException e) {

            cbFiltroCategoria.setValue(
                    "Todas las categorías"
            );

            System.err.println(
                    "Error al cargar categorías del filtro: "
                            + e.getMessage()
            );
        }
    }

    private void aplicarFiltros() {

        String texto =
                txtBuscar
                        .getText()
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        String estado =
                cbFiltroEstado
                        .getValue();

        String categoriaSeleccionada =
                cbFiltroCategoria
                        .getValue();

        productosFiltrados.setPredicate(
                producto -> {

                    boolean coincideBusqueda;

                    if (texto.isEmpty()) {

                        coincideBusqueda = true;

                    } else {

                        String codigo =
                                producto.getCodigo() == null
                                        ? ""
                                        : producto
                                        .getCodigo()
                                        .toLowerCase(
                                                Locale.ROOT
                                        );

                        String nombre =
                                producto.getNombre() == null
                                        ? ""
                                        : producto
                                        .getNombre()
                                        .toLowerCase(
                                                Locale.ROOT
                                        );

                        String categoria = "";

                        if (producto.getCategoria() != null
                                && producto
                                .getCategoria()
                                .getNombre() != null) {

                            categoria =
                                    producto
                                            .getCategoria()
                                            .getNombre()
                                            .toLowerCase(
                                                    Locale.ROOT
                                            );
                        }

                        coincideBusqueda =
                                codigo.contains(
                                        texto
                                )
                                        || nombre.contains(
                                        texto
                                )
                                        || categoria.contains(
                                        texto
                                );
                    }

                    boolean coincideEstado = true;

                    if ("Activos".equals(
                            estado
                    )) {

                        coincideEstado =
                                producto.isActivo();

                    } else if ("Inactivos".equals(
                            estado
                    )) {

                        coincideEstado =
                                !producto.isActivo();
                    }

                    boolean coincideCategoria = true;

                    if (categoriaSeleccionada != null
                            && !"Todas las categorías"
                            .equals(
                                    categoriaSeleccionada
                            )) {

                        coincideCategoria =
                                producto.getCategoria() != null
                                        && producto
                                        .getCategoria()
                                        .getNombre()
                                        .equalsIgnoreCase(
                                                categoriaSeleccionada
                                        );
                    }

                    return coincideBusqueda
                            && coincideEstado
                            && coincideCategoria;
                }
        );

        lblEstado.setText(
                "Productos encontrados: "
                        + productosFiltrados.size()
        );
    }

    private void mostrarAdvertencia(
            String mensaje
    ) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alerta.setTitle(
                "Validación"
        );

        alerta.setHeaderText(
                null
        );

        alerta.setContentText(
                mensaje
        );

        alerta.showAndWait();

        lblEstado.setText(
                mensaje
        );
    }

    private void mostrarError(
            String mensaje
    ) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alerta.setTitle(
                "Error"
        );

        alerta.setHeaderText(
                null
        );

        alerta.setContentText(
                mensaje
        );

        alerta.showAndWait();

        lblEstado.setText(
                mensaje
        );
    }

    private void mostrarExito(
            String titulo,
            String mensaje
    ) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alerta.setTitle(
                titulo
        );

        alerta.setHeaderText(
                null
        );

        alerta.setContentText(
                mensaje
        );

        alerta.showAndWait();

        lblEstado.setText(
                mensaje
        );
    }
}