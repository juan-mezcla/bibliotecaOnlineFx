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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML private TableView<Libro> TablaLibros;
    @FXML private TextField searchEstudiante, tituloLibroField, autorField;
    @FXML private ComboBox<String> selectGenero, selectGeneroLibro;
    @FXML private ListView<String> listaAutores;
    @FXML private DatePicker fechaPublicacion;
    @FXML private Button btnAnadirLibro, btnEditarLibro, btnEliminarLibro;
    @FXML private Button btnFiltroReservados, btnFiltroPrestados; // Añadidos los botones que faltaban
    @FXML private Label autorSelectLbl1;
    @FXML private Button btnSeleccionarImagen;
    @FXML private Label lblRutaImagen;

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
        
        // Inicializamos la lista filtrada
        listaFiltrada = new FilteredList<>(librosMaster, p -> true);
        TablaLibros.setItems(listaFiltrada);

        // Lógica del Buscador
        searchEstudiante.textProperty().addListener((obs, oldVal, newVal) -> {
            listaFiltrada.setPredicate(libro -> {
                if (newVal == null || newVal.isEmpty()) return true;
                if (libro.getEstudiante() != null) {
                    return libro.getEstudiante().getUsuario().toLowerCase().contains(newVal.toLowerCase());
                }
                return false;
            });
        });

        // Filtros rápidos
        btnFiltroPrestados.setOnAction(e -> listaFiltrada.setPredicate(Libro::isPrestado));
        btnFiltroReservados.setOnAction(e -> listaFiltrada.setPredicate(Libro::isReservado));
        selectGenero.setOnAction(e -> {
            String gen = selectGenero.getValue();
            listaFiltrada.setPredicate(libro -> libro.getGenero().equals(gen));
        });
    }

    @FXML
    void seleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Portada");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(btnSeleccionarImagen.getScene().getWindow());

        if (selectedFile != null) {
            this.archivoImagen = selectedFile;
            lblRutaImagen.setText(selectedFile.getName()); 
        } else {
            lblRutaImagen.setText("Imagen sin seleccionar");
        }
    }

    private void configurarTabla() {
        ((TableColumn<Libro, String>) TablaLibros.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<>("isbn"));
        ((TableColumn<Libro, String>) TablaLibros.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<>("titulo"));
        ((TableColumn<Libro, String>) TablaLibros.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<>("autor"));
        ((TableColumn<Libro, String>) TablaLibros.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<>("genero"));
        ((TableColumn<Libro, Integer>) TablaLibros.getColumns().get(4)).setCellValueFactory(new PropertyValueFactory<>("anio"));

        TableColumn<Libro, String> colEstado = (TableColumn<Libro, String>) TablaLibros.getColumns().get(5);
        colEstado.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().isPrestado() ? "Prestado" : (cell.getValue().isReservado() ? "Reservado" : "Disponible")
        ));

        TableColumn<Libro, String> colEstudiante = (TableColumn<Libro, String>) TablaLibros.getColumns().get(6);
        colEstudiante.setCellValueFactory(cell -> {
            User u = cell.getValue().getEstudiante();
            return new SimpleStringProperty(u != null ? u.getUsuario() : "---");
        });
    }

    private void cargarDatos() {
        librosMaster.setAll(dao.obtenerLibros());
        ObservableList<String> generos = FXCollections.observableArrayList("Ficción", "Ciencia", "Historia", "Fantasía");
        selectGenero.setItems(generos);
        selectGeneroLibro.setItems(generos);
    }

    private void configurarFormulario() {
        listaAutores.setItems(autoresEnEdicion);
        btnAnadirLibro.setOnAction(e -> ejecutarAccion("Añadir"));
        btnEditarLibro.setOnAction(e -> ejecutarAccion("Editar"));
        btnEliminarLibro.setOnAction(e -> ejecutarAccion("Eliminar"));
    }

    @FXML
    void anadirAutor(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !autorField.getText().isEmpty()) {
            autoresEnEdicion.add(autorField.getText().trim());
            autorField.clear();
        }
    }

    @FXML void seleccionarAutor(MouseEvent event) { /* ... */ }
    @FXML void eliminarAutorLista(MouseEvent event) { /* ... */ }

    private void ejecutarAccion(String tipo) {
        Libro seleccionado = TablaLibros.getSelectionModel().getSelectedItem();
        if ("Eliminar".equals(tipo) && seleccionado != null) {
            librosMaster.remove(seleccionado);
        }
        cargarDatos();
    }
}