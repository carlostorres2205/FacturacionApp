package ni.edu.uam.facturacionapp.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ni.edu.uam.facturacionapp.DAO.CategoriaDAO;
import ni.edu.uam.facturacionapp.DAO.ProductoDAO;
import ni.edu.uam.facturacionapp.model.Categoria;
import ni.edu.uam.facturacionapp.model.Producto;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

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
    private TextField txtRutaImagen;

    @FXML
    private CheckBox chkActivo;

    @FXML
    private Button btnSeleccionarImagen;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnLimpiar;

    @FXML
    private Label lblEstado;


    private final CategoriaDAO categoriaDAO =
            new CategoriaDAO();

    private final ProductoDAO productoDAO =
            new ProductoDAO();


    private File archivoImagen;


    @FXML
    public void initialize() {

        cargarCategorias();

        chkActivo.setSelected(true);
    }


    private void cargarCategorias() {

        List<Categoria> categorias = categoriaDAO.listar();

        if (categorias.isEmpty()) {

            crearCategoriasIniciales();

            categorias = categoriaDAO.listar();
        }

        cbCategoria.getItems().clear();
        cbCategoria.getItems().addAll(categorias);

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
    private void seleccionarImagen() {

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Seleccionar imagen del producto"
        );

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imágenes",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );


        Stage ventana =
                (Stage) btnSeleccionarImagen
                        .getScene()
                        .getWindow();


        File archivoSeleccionado =
                fileChooser.showOpenDialog(
                        ventana
                );


        if (archivoSeleccionado != null) {

            archivoImagen =
                    archivoSeleccionado;

            txtRutaImagen.setText(
                    archivoSeleccionado
                            .getAbsolutePath()
            );

            lblEstado.setText(
                    "Imagen seleccionada correctamente."
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

            String rutaImagen =
                    txtRutaImagen
                            .getText()
                            .trim();

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

            producto.setRutaImagen(
                    rutaImagen
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

        cbCategoria.setValue(null);

        txtPrecio.clear();

        txtExistencia.clear();

        txtRutaImagen.clear();

        chkActivo.setSelected(true);

        archivoImagen = null;

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

        alerta.setHeaderText(null);

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
    private void crearCategoriasIniciales() {

        Categoria electronica =
                new Categoria(
                        null,
                        "Electrónica",
                        true
                );

        Categoria alimentos =
                new Categoria(
                        null,
                        "Alimentos",
                        true
                );

        Categoria limpieza =
                new Categoria(
                        null,
                        "Limpieza",
                        true
                );

        Categoria hogar =
                new Categoria(
                        null,
                        "Hogar",
                        true
                );

        Categoria oficina =
                new Categoria(
                        null,
                        "Oficina",
                        true
                );

        categoriaDAO.guardar(electronica);
        categoriaDAO.guardar(alimentos);
        categoriaDAO.guardar(limpieza);
        categoriaDAO.guardar(hogar);
        categoriaDAO.guardar(oficina);
    }
}
