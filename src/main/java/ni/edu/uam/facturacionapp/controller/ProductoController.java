package ni.edu.uam.facturacionapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import ni.edu.uam.facturacionapp.dao.CategoriaDAO;
import ni.edu.uam.facturacionapp.dao.ProductoDAO;
import ni.edu.uam.facturacionapp.model.Categoria;
import ni.edu.uam.facturacionapp.model.Producto;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.sql.SQLException;
import java.util.Locale;

import java.math.BigDecimal;
import java.util.List;
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




    private final CategoriaDAO categoriaDAO =
            new CategoriaDAO();

    private final ProductoDAO productoDAO =
            new ProductoDAO();




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


        if (respuesta.isPresent()
                && respuesta.get() == ButtonType.OK) {

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
                        "No se pudo eliminar el producto."
                );
            }
        }
    }


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


    private void cargarCategorias() {

        List<Categoria> categorias =
                categoriaDAO.listar();

        cbCategoria.getItems().clear();

        cbCategoria.getItems().addAll(
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
                    "Debe ingresar el nombre de la categoría."
            );

            return;
        }


        for (Categoria categoria :
                cbCategoria.getItems()) {

            if (categoria
                    .getNombre()
                    .equalsIgnoreCase(nombre)) {

                mostrarAdvertencia(
                        "La categoría ya existe."
                );

                cbCategoria.setValue(
                        categoria
                );

                return;
            }
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


            for (Categoria categoria :
                    cbCategoria.getItems()) {

                if (categoria
                        .getId()
                        .equals(
                                nuevaCategoria.getId()
                        )) {

                    cbCategoria.setValue(
                            categoria
                    );

                    break;
                }
            }


            lblEstado.setText(
                    "Categoría agregada correctamente."
            );

        } else {

            mostrarError(
                    "No se pudo guardar la categoría."
            );
        }
    }


    @FXML
    private void guardarProducto() {

        if (!validarFormulario()) {
            return;
        }


        try {

            String codigo =
                    txtCodigo
                            .getText()
                            .trim();

            String nombre =
                    txtNombre
                            .getText()
                            .trim();

            Categoria categoria =
                    cbCategoria
                            .getValue();

            BigDecimal precioVenta =
                    new BigDecimal(
                            txtPrecio
                                    .getText()
                                    .trim()
                    );

            int existencia =
                    Integer.parseInt(
                            txtExistencia
                                    .getText()
                                    .trim()
                    );

            boolean activo =
                    chkActivo
                            .isSelected();


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
                    precioVenta
            );

            producto.setExistencia(
                    existencia
            );

            /*
             * Ya no estamos trabajando
             * con selección de imágenes.
             */
            producto.setRutaImagen(
                    null
            );

            producto.setActivo(
                    activo
            );

            if (productoDAO.existeCodigo(
                    producto.getCodigo()
            )) {

                mostrarAdvertencia(
                        "Ya existe un producto con ese código."
                );

                return;
            }


            boolean guardado =


                    productoDAO.guardar(
                            producto
                    );


            if (guardado) {

                Alert alerta =
                        new Alert(
                                Alert.AlertType.INFORMATION
                        );


                alerta.setTitle(
                        "Producto guardado"
                );

                alerta.setHeaderText(
                        "Registro exitoso"
                );

                alerta.setContentText(
                        "El producto "
                                + producto.getNombre()
                                + " fue guardado correctamente."
                );

                alerta.showAndWait();


                lblEstado.setText(
                        "Producto guardado correctamente."
                );
                cargarProductos();

                limpiarFormulario();

            } else {

                mostrarError(
                        "No se pudo guardar el producto."
                );
            }

        } catch (NumberFormatException e) {

            mostrarError(
                    "Precio y existencia deben contener valores numéricos."
            );
        } catch (SQLException e) {

            mostrarError(
                    "No fue posible comprobar o guardar el producto."
            );

            System.err.println(
                    e.getMessage()
            );
    }
    }



    private boolean validarFormulario() {

        if (txtCodigo
                .getText()
                .trim()
                .isEmpty()) {

            mostrarAdvertencia(
                    "Debe ingresar el código."
            );

            txtCodigo.requestFocus();

            return false;
        }


        if (txtNombre
                .getText()
                .trim()
                .isEmpty()) {

            mostrarAdvertencia(
                    "Debe ingresar el nombre."
            );

            txtNombre.requestFocus();

            return false;
        }


        if (cbCategoria
                .getValue() == null) {

            mostrarAdvertencia(
                    "Debe seleccionar una categoría."
            );

            return false;
        }


        if (txtPrecio
                .getText()
                .trim()
                .isEmpty()) {

            mostrarAdvertencia(
                    "Debe ingresar el precio."
            );

            txtPrecio.requestFocus();

            return false;
        }


        if (txtExistencia
                .getText()
                .trim()
                .isEmpty()) {

            mostrarAdvertencia(
                    "Debe ingresar la existencia."
            );

            txtExistencia.requestFocus();

            return false;
        }


        try {

            BigDecimal precio =
                    new BigDecimal(
                            txtPrecio
                                    .getText()
                                    .trim()
                    );

            if (precio.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                mostrarAdvertencia(
                        "El precio no puede ser negativo."
                );

                return false;
            }

        } catch (NumberFormatException e) {

            mostrarAdvertencia(
                    "El precio debe ser un valor numérico."
            );

            return false;
        }


        try {

            int existencia =
                    Integer.parseInt(
                            txtExistencia
                                    .getText()
                                    .trim()
                    );

            if (existencia < 0) {

                mostrarAdvertencia(
                        "La existencia no puede ser negativa."
                );

                return false;
            }

        } catch (NumberFormatException e) {

            mostrarAdvertencia(
                    "La existencia debe ser un número entero."
            );

            return false;
        }


        return true;
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

        lblEstado.setText(
                "Formulario limpiado."
        );

        txtCodigo.requestFocus();
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
                "Ocurrió un problema"
        );

        alerta.setContentText(
                mensaje
        );

        alerta.showAndWait();

        lblEstado.setText(
                mensaje
        );
    }
    private void configurarTabla() {

        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>("codigo")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precioVenta")
        );

        colExistencia.setCellValueFactory(
                new PropertyValueFactory<>("existencia")
        );

        colActivo.setCellValueFactory(
                new PropertyValueFactory<>("activo")
        );

        colCategoria.setCellValueFactory(
                datos -> new javafx.beans.property.SimpleStringProperty(
                        datos.getValue()
                                .getCategoria()
                                .getNombre()
                )
        );
    }

    private void cargarProductos() {

        productos.clear();

        productos.addAll(
                productoDAO.listar()
        );
    }

    private final ObservableList<Producto> productos =
            FXCollections.observableArrayList();

    private FilteredList<Producto> productosFiltrados;

    private void configurarFiltros() {

        cbFiltroEstado.getItems().addAll(
                "Todos",
                "Activos",
                "Inactivos"
        );

        cbFiltroEstado.setValue("Todos");


        cbFiltroCategoria.getItems().add(
                "Todas las categorías"
        );

        for (Categoria categoria : categoriaDAO.listar()) {

            cbFiltroCategoria.getItems().add(
                    categoria.getNombre()
            );
        }

        cbFiltroCategoria.setValue(
                "Todas las categorías"
        );


        cbFiltroEstado.setOnAction(
                event -> aplicarFiltros()
        );

        cbFiltroCategoria.setOnAction(
                event -> aplicarFiltros()
        );
    }

    private void aplicarFiltros() {

        String texto =
                txtBuscar
                        .getText()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        String estado =
                cbFiltroEstado.getValue();

        String categoriaSeleccionada =
                cbFiltroCategoria.getValue();


        productosFiltrados.setPredicate(
                producto -> {

                    // -------------------------
                    // BÚSQUEDA
                    // -------------------------

                    boolean coincideBusqueda;

                    if (texto.isEmpty()) {

                        coincideBusqueda = true;

                    } else {

                        String codigo =
                                producto.getCodigo() == null
                                        ? ""
                                        : producto
                                          .getCodigo()
                                          .toLowerCase(Locale.ROOT);

                        String nombre =
                                producto.getNombre() == null
                                        ? ""
                                        : producto
                                          .getNombre()
                                          .toLowerCase(Locale.ROOT);

                        String categoria = "";

                        if (producto.getCategoria() != null
                                && producto.getCategoria().getNombre() != null) {

                            categoria =
                                    producto
                                            .getCategoria()
                                            .getNombre()
                                            .toLowerCase(Locale.ROOT);
                        }

                        coincideBusqueda =
                                codigo.contains(texto)
                                        || nombre.contains(texto)
                                        || categoria.contains(texto);
                    }


                    // -------------------------
                    // ESTADO
                    // -------------------------

                    boolean coincideEstado = true;

                    if ("Activos".equals(estado)) {

                        coincideEstado =
                                producto.isActivo();

                    } else if ("Inactivos".equals(estado)) {

                        coincideEstado =
                                !producto.isActivo();
                    }


                    // -------------------------
                    // CATEGORÍA
                    // -------------------------

                    boolean coincideCategoria = true;

                    if (categoriaSeleccionada != null
                            && !"Todas las categorías"
                            .equals(categoriaSeleccionada)) {

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

    private void actualizarCategoriasFiltro() {

        String seleccionActual =
                cbFiltroCategoria.getValue();

        cbFiltroCategoria.getItems().clear();

        cbFiltroCategoria.getItems().add(
                "Todas las categorías"
        );

        for (Categoria categoria : categoriaDAO.listar()) {

            cbFiltroCategoria.getItems().add(
                    categoria.getNombre()
            );
        }

        if (seleccionActual != null
                && cbFiltroCategoria
                .getItems()
                .contains(seleccionActual)) {

            cbFiltroCategoria.setValue(
                    seleccionActual
            );

        } else {

            cbFiltroCategoria.setValue(
                    "Todas las categorías"
            );
        }
    }
}