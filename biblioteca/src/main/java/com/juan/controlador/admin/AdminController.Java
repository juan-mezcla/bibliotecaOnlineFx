package com.juan.controlador.admin;

import com.juan.dao.DaoBliblioteca;
import com.juan.dao.DaoBlibliotecaImpl;
import com.juan.model.Libro;
import com.juan.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private TableView<Libro> TablaLibros;
    @FXML
    private TextField searchEstudiante, tituloLibroField, autorField, isbnField; // Añadido isbnField
    @FXML
    private ComboBox<String> selectGenero, selectGeneroLibro;
    @FXML
    private ListView<String> listaAutores;
    @FXML
    private DatePicker fechaPublicacion;
    @FXML
    private Button btnAnadirLibro, btnEditarLibro, btnEliminarLibro, btnCerrarSesion;
    @FXML
    private Button btnFiltroReservados, btnFiltroPrestados, btnSeleccionarImagen;
    @FXML
    private Label autorSelectLbl1, lblRutaImagen;

    private DaoBliblioteca dao = new DaoBlibliotecaImpl();
    private ObservableList<Libro> librosMaster = FXCollections.observableArrayList();
    private ObservableList<String> autoresEnEdicion = FXCollections.observableArrayList();
    private FilteredList<Libro> listaFiltrada;
    private File archivoImagen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarDatos();
        configurarFormulario();

        TablaLibros.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                rellenarCampos(newSelection);
            }
        });

        listaFiltrada = new FilteredList<>(librosMaster, p -> true);
        TablaLibros.setItems(listaFiltrada);

        // Listeners de búsqueda
        searchEstudiante.textProperty()
                .addListener((obs, oldVal, newVal) -> aplicarFiltros(newVal, selectGenero.getValue()));
        selectGenero.setOnAction(e -> aplicarFiltros(searchEstudiante.getText(), selectGenero.getValue()));

        // Acciones de botones principales
        btnAnadirLibro.setOnAction(e -> manejarAnadirLibro());
        btnEliminarLibro.setOnAction(e -> manejarEliminarLibro());

        btnEditarLibro.setOnAction(e -> manejarEditarLibro());

        btnFiltroPrestados.setOnAction(e -> listaFiltrada.setPredicate(Libro::isPrestado));
        btnFiltroReservados.setOnAction(e -> listaFiltrada.setPredicate(Libro::isReservado));
    }

    private byte[] leerBytesImagen(File archivo) {
    if (archivo == null) return null;
    try {
        return java.nio.file.Files.readAllBytes(archivo.toPath());
    } catch (java.io.IOException e) {
        System.err.println("Error al leer los bytes de la imagen: " + e.getMessage());
        return null;
    }
}

    @FXML
    void closeSesion(ActionEvent event) {
        try {
            // Ajusta esta ruta a la ubicación real de tu login.fxml
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/juan/component_login_registro/login_registro.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Biblioteca");
            stage.show();

            // Cerrar la ventana actual de administración
            Stage currentStage = (Stage) btnCerrarSesion.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cerrar sesión: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
void manejarAnadirLibro() {
    if (tituloLibroField.getText().isEmpty() || selectGeneroLibro.getValue() == null) {
        mostrarAlerta("Campos incompletos", "Por favor, rellena Título y Género.", Alert.AlertType.WARNING);
        return;
    }

    Libro nuevoLibro = new Libro();
    String isbnAleatorio = generarISBNAleatorio();
    nuevoLibro.setIsbn(isbnAleatorio);
    nuevoLibro.setTitulo(tituloLibroField.getText());
    nuevoLibro.setGenero(selectGeneroLibro.getValue());
    nuevoLibro.setAutores(new java.util.ArrayList<>(autoresEnEdicion));

    // ESTO ES LO QUE FALTA: Convertir el archivo seleccionado a bytes
    if (archivoImagen != null) {
        nuevoLibro.setImagen(leerBytesImagen(archivoImagen));
    }

    if (fechaPublicacion.getValue() != null) {
        nuevoLibro.setFechaPublicacion(fechaPublicacion.getValue());
    }

    boolean exito = dao.insertarLibro(nuevoLibro);
    if (exito) {
        mostrarAlerta("Éxito", "Libro añadido con ISBN: " + isbnAleatorio, Alert.AlertType.INFORMATION);
        limpiarFormulario();
        cargarDatos();
    } else {
        mostrarAlerta("Error", "No se pudo guardar el libro.", Alert.AlertType.ERROR);
    }
}

    @FXML
    void manejarEditarLibro() {
        Libro seleccionado = TablaLibros.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un libro de la tabla para editar.", Alert.AlertType.WARNING);
            return;
        }

        // 1. Actualizamos los datos del objeto que ya está en la lista
        seleccionado.setTitulo(tituloLibroField.getText());
        seleccionado.setGenero(selectGeneroLibro.getValue());
        seleccionado.setFechaPublicacion(fechaPublicacion.getValue());

        // Importante: Crear una nueva lista para evitar problemas de persistencia con
        // Hibernate
        seleccionado.setAutores(new java.util.ArrayList<>(autoresEnEdicion));

        // 2. Llamamos al DAO para persistir los cambios (usa em.merge)
        boolean exito = dao.actualizarLibro(seleccionado);

        if (exito) {
            mostrarAlerta("Éxito", "Libro actualizado correctamente.", Alert.AlertType.INFORMATION);

            // 3. Forzar el refresco visual de la tabla
            TablaLibros.refresh();

            // Opcional: limpiar el formulario tras editar
            limpiarFormulario();
            TablaLibros.getSelectionModel().clearSelection();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el libro en la base de datos.", Alert.AlertType.ERROR);
        }
    }

    private String generarISBNAleatorio() {
        long prefijo = 978;
        // Generamos un número aleatorio de 10 dígitos
        long cuerpo = (long) (Math.random() * 10000000000L);
        return prefijo + String.format("%010d", cuerpo);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // --- MÉTODOS DE APOYO (Ya existentes en tu código pero mantenidos para
    // coherencia) ---

    private void aplicarFiltros(String nombreEstudiante, String genero) {
        listaFiltrada.setPredicate(libro -> {
            boolean matchNombre = (nombreEstudiante == null || nombreEstudiante.isEmpty()) ||
                    (libro.getEstudiante() != null && libro.getEstudiante().getUsuario().toLowerCase()
                            .contains(nombreEstudiante.toLowerCase()));

            boolean matchGenero = (genero == null || genero.equals("Todos")) ||
                    (libro.getGenero() != null && libro.getGenero().equals(genero));

            return matchNombre && matchGenero;
        });
    }

    private void cargarDatos() {
        librosMaster.setAll(dao.obtenerLibros());
        ObservableList<String> generosBase = FXCollections.observableArrayList("Thriller", "Acción", "Terror",
                "Fantasía", "Ciencia Ficción");
        selectGeneroLibro.setItems(generosBase);

        ObservableList<String> generosFiltro = FXCollections.observableArrayList("Todos");
        generosFiltro.addAll(generosBase);
        selectGenero.setItems(generosFiltro);
    }

    private void configurarTabla() {
        // 0. ISBN
        ((TableColumn<Libro, String>) TablaLibros.getColumns().get(0)).setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));

        // 1. Título
        ((TableColumn<Libro, String>) TablaLibros.getColumns().get(1)).setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getTitulo()));

        // 2. Autores (Uniendo la lista en un String)
        ((TableColumn<Libro, String>) TablaLibros.getColumns().get(2)).setCellValueFactory(cellData -> {
            var autores = cellData.getValue().getAutores();
            return new SimpleStringProperty(autores != null ? String.join(", ", autores) : "N/A");
        });

        // 3. Género
        ((TableColumn<Libro, String>) TablaLibros.getColumns().get(3)).setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getGenero()));

        // 4. Año (Asegúrate de que getFechaPublicacion() exista en Libro)
        ((TableColumn<Libro, String>) TablaLibros.getColumns().get(4)).setCellValueFactory(cellData -> {
            var fecha = cellData.getValue().getFechaPublicacion();
            return new SimpleStringProperty(fecha != null ? String.valueOf(fecha.getYear()) : "N/A");
        });

        // 5. Estado
        ((TableColumn<Libro, String>) TablaLibros.getColumns().get(5))
                .setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isPrestado() ? "Prestado"
                        : (cell.getValue().isReservado() ? "Reservado" : "Disponible")));

        // 6. Estudiante
        ((TableColumn<Libro, String>) TablaLibros.getColumns().get(6))
                .setCellValueFactory(cell -> new SimpleStringProperty(
                        cell.getValue().getEstudiante() != null ? cell.getValue().getEstudiante().getUsuario()
                                : "---"));
    }

    private void limpiarFormulario() {
        // QUITAMOS isbnField.clear() porque el objeto es null y daría error
        tituloLibroField.clear();
        autorField.clear();
        if (selectGeneroLibro != null)
            selectGeneroLibro.setValue(null);
        autoresEnEdicion.clear();
        if (fechaPublicacion != null)
            fechaPublicacion.setValue(null);
        lblRutaImagen.setText("Imagen sin seleccionar");
        archivoImagen = null;
    }

    private void configurarFormulario() {
        listaAutores.setItems(autoresEnEdicion);
    }

    @FXML
    void anadirAutor(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !autorField.getText().trim().isEmpty()) {
            autoresEnEdicion.add(autorField.getText().trim());
            autorField.clear();
        }
    }

    @FXML
    void eliminarAutorLista(MouseEvent event) {
        String seleccionado = listaAutores.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            autoresEnEdicion.remove(seleccionado);
        }
    }

    @FXML
    void seleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(btnSeleccionarImagen.getScene().getWindow());
        if (selectedFile != null) {
            this.archivoImagen = selectedFile;
            lblRutaImagen.setText(selectedFile.getName());
        }
    }

    @FXML
    void seleccionarAutor(MouseEvent event) {
        String seleccionado = listaAutores.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            autorSelectLbl1.setText("Seleccionado: " + seleccionado);
        }
    }

    @FXML
    void manejarEliminarLibro() {
        Libro seleccionado = TablaLibros.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            if (dao.eliminarLibro(seleccionado.getIsbn())) {
                librosMaster.remove(seleccionado);
            }
        }
    }

    private void rellenarCampos(Libro libro) {
        tituloLibroField.setText(libro.getTitulo());
        selectGeneroLibro.setValue(libro.getGenero());
        fechaPublicacion.setValue(libro.getFechaPublicacion());

        // Sincronizar la lista de autores del ListView con los del libro seleccionado
        autoresEnEdicion.setAll(libro.getAutores());

        // Si el campo isbnField existe en tu FXML y está inyectado:
        if (isbnField != null) {
            isbnField.setText(libro.getIsbn());
            isbnField.setDisable(true); // Desactivar para que no cambien la PK
        }
    }
}