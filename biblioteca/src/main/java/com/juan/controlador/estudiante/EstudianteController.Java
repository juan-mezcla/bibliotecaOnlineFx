package com.juan.controlador.estudiante;

import com.juan.dao.DaoBliblioteca;
import com.juan.dao.DaoBlibliotecaImpl;
import com.juan.model.Libro;
import com.juan.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EstudianteController implements Initializable {

    @FXML
    private TableView<Libro> TablaLibros;
    @FXML
    private ComboBox<String> selectGenero;
    @FXML
    private TextField searchLibro;
    @FXML
    private Label tituloLibro, isbn, genero, anio, fechaPrestamo, estadoLibro;
    @FXML
    private ListView<String> listaAutores;
    @FXML
    private ImageView portadaLibro;

    // Botones de acción
    @FXML
    private Button btnReservar, btnPrestamo, btnDevolver, btnCerrarSesion;
    // Botones de filtrado de lista
    @FXML
    private Button btnLibrosDisponibles, btnMisLibros;

    private DaoBliblioteca dao = new DaoBlibliotecaImpl();
    private ObservableList<Libro> masterData = FXCollections.observableArrayList();
    private static User usuarioSesion;

    public static void setUsuarioSesion(User user) {
        usuarioSesion = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarDatosIniciales();
        configurarFiltros();

        // Listener de selección
        TablaLibros.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> mostrarDetallesLibro(newSelection));

        // --- Eventos de Botones de Filtrado ---
        btnLibrosDisponibles.setOnAction(e -> filtrarDisponibles());
        btnMisLibros.setOnAction(e -> filtrarMisLibros());

        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnAction(e -> closeSesion());
        }
    }

    // --- MÉTODOS DE FILTRADO (Botones inferiores de la tabla) ---

    private void filtrarDisponibles() {
        // Muestra libros que no están ni prestados ni reservados
        ObservableList<Libro> disponibles = masterData.filtered(l -> !l.isPrestado() && !l.isReservado());
        TablaLibros.setItems(disponibles);
    }

    private void filtrarMisLibros() {
        if (usuarioSesion == null)
            return;
        // Muestra libros donde el estudiante asignado coincide con el de la sesión
        ObservableList<Libro> misLibros = masterData
                .filtered(l -> l.getEstudiante() != null && l.getEstudiante().getIdUser() == usuarioSesion.getIdUser());
        TablaLibros.setItems(misLibros);
    }

    // --- MÉTODOS DE ACCIÓN (Botones bajo los detalles) ---
    @FXML
    private void manejarAlargarPrestamo() {
        // Forzamos la obtención del item actual del modelo de selección
        Libro seleccionado = TablaLibros.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            alertar("Atención", "Por favor, selecciona un libro de la lista primero.", Alert.AlertType.WARNING);
            return;
        }

        if (seleccionado.isPrestado()) {
            if (dao.alargarPrestamo(seleccionado.getIsbn(), 7)) {
                alertar("Éxito", "Préstamo extendido 7 días más.", Alert.AlertType.INFORMATION);
                cargarDatosIniciales(); // Esto refresca la tabla y limpia la selección
            } else {
                alertar("Error", "No se pudo alargar el préstamo en la base de datos.", Alert.AlertType.ERROR);
            }
        } else {
            alertar("Atención", "Este libro no está marcado como prestado.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void manejarReserva() {
        Libro seleccionado = TablaLibros.getSelectionModel().getSelectedItem();

        if (seleccionado == null || usuarioSesion == null) {
            alertar("Atención", "Selecciona un libro primero.", Alert.AlertType.WARNING);
            return;
        }

        // Evitar que el usuario reserve lo que ya tiene
        boolean esMio = seleccionado.getEstudiante() != null &&
                seleccionado.getEstudiante().getIdUser() == usuarioSesion.getIdUser();

        if (esMio) {
            alertar("Atención", "Ya tienes este libro en tu posesión.", Alert.AlertType.WARNING);
            return;
        }

        // ESCENARIO 1: DISPONIBLE -> PRESTAR
        if (!seleccionado.isPrestado() && !seleccionado.isReservado()) {
            LocalDate fechaLimite = LocalDate.now().plusDays(15);
            if (dao.prestarLibro(seleccionado.getIsbn(), usuarioSesion, fechaLimite)) {
                alertar("Éxito", "Préstamo realizado hasta " + fechaLimite, Alert.AlertType.INFORMATION);
            }
        }
        // ESCENARIO 2: PRESTADO -> RESERVAR
        else if (seleccionado.isPrestado() && !seleccionado.isReservado()) {
            if (dao.reservarLibro(seleccionado.getIsbn(), usuarioSesion)) {
                alertar("Reserva Realizada", "El libro está prestado. Se te avisará cuando lo devuelvan.",
                        Alert.AlertType.INFORMATION);
            }
        }

        cargarDatosIniciales();
    }

    @FXML
    private void manejarDevolucion() {
        Libro seleccionado = TablaLibros.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            if (dao.devolverLibro(seleccionado.getIsbn())) {
                alertar("Éxito", "Libro devuelto.", Alert.AlertType.INFORMATION);
                cargarDatosIniciales();
            }
        }
    }

    // --- MÉTODOS DE CONFIGURACIÓN Y UI ---

    private void mostrarDetallesLibro(Libro libro) {
        if (libro != null) {
            tituloLibro.setText(libro.getTitulo());
            isbn.setText("ISBN: " + libro.getIsbn());
            genero.setText("Género: " + libro.getGenero());
            anio.setText(
                    "Año: " + (libro.getFechaPublicacion() != null ? libro.getFechaPublicacion().getYear() : "N/A"));
            estadoLibro.setText("Estado: "
                    + (libro.isPrestado() ? "Prestado" : (libro.isReservado() ? "Reservado" : "Disponible")));
            fechaPrestamo.setText("Límite: " + (libro.getLimitePrestamo() != null ? libro.getLimitePrestamo() : "N/A"));

            // Autores
            if (libro.getAutores() != null)
                listaAutores.getItems().setAll(libro.getAutores());
            else
                listaAutores.getItems().clear();

            // Imagen
            if (libro.getImagen() != null && libro.getImagen().length > 0) {
                portadaLibro.setImage(new Image(new ByteArrayInputStream(libro.getImagen())));
            } else {
                portadaLibro.setImage(null);
            }

            // ¿Lo tengo yo prestado?
    boolean loTengoPrestado = libro.getEstudiante() != null && 
        libro.getEstudiante().getIdUser() == usuarioSesion.getIdUser();
    
    // ¿Lo tengo yo reservado?
    boolean loTengoReservado = libro.getUsuarioReserva() != null && 
        libro.getUsuarioReserva().getIdUser() == usuarioSesion.getIdUser();

    // ESTADO DE BOTONES
    
    // Reservar: Disponible si está prestado por OTRO y nadie lo ha reservado aún
    btnReservar.setDisable(libro.isReservado() || !libro.isPrestado() || loTengoPrestado);
    
    // Prestar: (Si usas el mismo botón para prestar cuando está libre)
    if (!libro.isPrestado() && !libro.isReservado()) {
        btnReservar.setDisable(false); // Permitir préstamo directo
    }

    // Devolver y Alargar: Solo si yo soy el que lo tiene prestado físicamente
    btnDevolver.setDisable(!loTengoPrestado);
    btnPrestamo.setDisable(!loTengoPrestado);
        }
    }

    private void configurarTabla() {
        TableColumn<Libro, String> colIsbn = (TableColumn<Libro, String>) TablaLibros.getColumns().get(0);
        TableColumn<Libro, String> colTitulo = (TableColumn<Libro, String>) TablaLibros.getColumns().get(1);
        TableColumn<Libro, String> colAutores = (TableColumn<Libro, String>) TablaLibros.getColumns().get(2);
        TableColumn<Libro, String> colGenero = (TableColumn<Libro, String>) TablaLibros.getColumns().get(3);
        TableColumn<Libro, String> colAnio = (TableColumn<Libro, String>) TablaLibros.getColumns().get(4);
        TableColumn<Libro, String> colEstado = (TableColumn<Libro, String>) TablaLibros.getColumns().get(5);

        colIsbn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getIsbn()));
        colTitulo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitulo()));
        colAutores.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getAutores() != null ? String.join(", ", cell.getValue().getAutores()) : "---"));
        colGenero.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGenero()));
        colAnio.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFechaPublicacion() != null
                ? String.valueOf(cell.getValue().getFechaPublicacion().getYear())
                : "N/A"));
        colEstado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isPrestado() ? "Prestado"
                : (cell.getValue().isReservado() ? "Reservado" : "Disponible")));
    }

    private void cargarDatosIniciales() {
        masterData.setAll(dao.obtenerLibros());
        TablaLibros.setItems(masterData);

        // Cargar ComboBox de géneros (manteniendo el código que ya tenías)
        ObservableList<String> opciones = FXCollections.observableArrayList("Todos", "Thriller", "Acción", "Terror",
                "Fantasía", "Ciencia Ficción", "Romance");
        List<String> extras = masterData.stream().map(Libro::getGenero).distinct()
                .filter(g -> g != null && !opciones.contains(g)).collect(Collectors.toList());
        opciones.addAll(extras);
        selectGenero.setItems(opciones);
        selectGenero.setValue("Todos");
    }

    private void configurarFiltros() {
        searchLibro.textProperty().addListener((obs, old, newVal) -> filtrarLista(newVal, selectGenero.getValue()));
        selectGenero.valueProperty().addListener((obs, old, newVal) -> filtrarLista(searchLibro.getText(), newVal));
    }

    private void filtrarLista(String texto, String gen) {
        ObservableList<Libro> filtered = masterData.filtered(l -> {
            boolean matchT = texto == null || texto.isEmpty()
                    || l.getTitulo().toLowerCase().contains(texto.toLowerCase());
            boolean matchG = gen == null || gen.equals("Todos") || (l.getGenero() != null && l.getGenero().equals(gen));
            return matchT && matchG;
        });
        TablaLibros.setItems(filtered);
    }

    @FXML
    void closeSesion() {
        try {
            Parent root = FXMLLoader
                    .load(getClass().getResource("/com/juan/component_login_registro/login_registro.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            ((Stage) btnCerrarSesion.getScene().getWindow()).close();
            usuarioSesion = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alertar(String titulo, String cont, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(cont);
        a.show();
    }
}