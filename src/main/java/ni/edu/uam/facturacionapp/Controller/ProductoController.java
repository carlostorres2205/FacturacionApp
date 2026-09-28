package ni.edu.uam.facturacionapp.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import ni.edu.uam.facturacionapp.DAO.CategoriaDAO;
import ni.edu.uam.facturacionapp.DAO.ProductoDAO;
import ni.edu.uam.facturacionapp.model.Categoria;
import ni.edu.uam.facturacionapp.model.Producto;
import javafx.scene.control.cell.PropertyValueFactory;

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


    private final CategoriaDAO categoriaDAO =
            new CategoriaDAO();

    private final ProductoDAO productoDAO =
            new ProductoDAO();


    @FXML
    public void initialize() {

        cargarCategorias();
        cargarCategorias();

        configurarTabla();

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
            ) < 0) {

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

        List<Producto> productos =
                productoDAO.listar();

        tvProductos.getItems().clear();

        tvProductos.getItems().addAll(
                productos
        );
    }
}