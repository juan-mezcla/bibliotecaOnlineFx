package com.juan.controlador.estudiante;

import com.juan.dao.DaoBliblioteca;
import com.juan.dao.DaoBlibliotecaImpl;
import com.juan.model.Libro;
import com.juan.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EstudianteController implements Initializable {

    // --- Elementos FXML ---
    @FXML private TableView<Libro> TablaLibros;
    @FXML private ComboBox<String> selectGenero;
    @FXML private TextField searchLibro;
    @FXML private Label tituloLibro, isbn, genero, anio, fechaPrestamo, estadoLibro;
    @FXML private ListView<String> listaAutores;
    @FXML private ImageView portadaLibro;
    @FXML private Button btnReservar, btnPrestamo, btnDevolver, btnMisLibros1;

    private DaoBliblioteca dao = new DaoBlibliotecaImpl();
    private ObservableList<Libro> masterData = FXCollections.observableArrayList();
    
    // Usuario que ha iniciado sesión (deberías pasarlo desde el Login)
    private static User usuarioSesion;

    public static void setUsuarioSesion(User user) {
        usuarioSesion = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarDatosIniciales();
        configurarFiltros();
        
        // Listener para cuando seleccionamos un libro en la tabla
        TablaLibros.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> mostrarDetallesLibro(newSelection)
        );

        // Acciones de botones
        btnDevolver.setOnAction(e -> manejarDevolucion());
        btnReservar.setOnAction(e -> manejarReserva());
        btnMisLibros1.setOnAction(e -> cerrarSesion());
    }

    private void configurarTabla() {
    // 1. Obtenemos todas las columnas por su índice (0 a 5) según el orden del FXML
    TableColumn<Libro, String> colIsbn = (TableColumn<Libro, String>) TablaLibros.getColumns().get(0);
    TableColumn<Libro, String> colTitulo = (TableColumn<Libro, String>) TablaLibros.getColumns().get(1);
    TableColumn<Libro, String> colAutores = (TableColumn<Libro, String>) TablaLibros.getColumns().get(2);
    TableColumn<Libro, String> colGenero = (TableColumn<Libro, String>) TablaLibros.getColumns().get(3);
    TableColumn<Libro, Integer> colAnio = (TableColumn<Libro, Integer>) TablaLibros.getColumns().get(4);
    TableColumn<Libro, String> colEstado = (TableColumn<Libro, String>) TablaLibros.getColumns().get(5);

    // 2. Vinculamos las columnas con los atributos de tu clase Libro
    // Asegúrate de que los nombres coincidan exactamente con tus variables en Libro.java
    colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
    colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
    colAutores.setCellValueFactory(new PropertyValueFactory<>("autor")); // Cambiar a "autores" si ese es el nombre en tu modelo
    colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
    colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));

    // 3. Configuración dinámica para la columna Estado
    // Usamos una celda personalizada para transformar los booleanos en texto para el usuario
    colEstado.setCellValueFactory(cellData -> {
        Libro libro = cellData.getValue();
        String estadoActual = "Disponible";
        
        if (libro.isPrestado()) {
            estadoActual = "Prestado";
        } else if (libro.isReservado()) {
            estadoActual = "Reservado";
        }
        
        return new javafx.beans.property.SimpleStringProperty(estadoActual);
    });
}

    private void cargarDatosIniciales() {
        masterData.setAll(dao.obtenerLibros());
        TablaLibros.setItems(masterData);
        
        // Llenar géneros únicos en el ComboBox
        ObservableList<String> generos = FXCollections.observableArrayList(
            masterData.stream().map(Libro::getGenero).distinct().collect(Collectors.toList())
        );
        selectGenero.setItems(generos);
    }

    private void mostrarDetallesLibro(Libro libro) {
        if (libro != null) {
            tituloLibro.setText("Título: " + libro.getTitulo());
            isbn.setText("ISBN: " + libro.getIsbn());
            genero.setText("Género: " + libro.getGenero());
            anio.setText("Año: " + libro.getAnio());
            estadoLibro.setText("Estado: " + (libro.isPrestado() ? "Prestado" : "Disponible"));
            
            if (libro.getLimitePrestamo() != null) {
                fechaPrestamo.setText("Límite: " + libro.getLimitePrestamo().toString());
            } else {
                fechaPrestamo.setText("Límite: N/A");
            }

            // Actualizar botones según estado
            btnReservar.setDisable(libro.isPrestado() || libro.isReservado());
            btnDevolver.setDisable(!libro.isPrestado());
        }
    }

    private void manejarReserva() {
        Libro seleccionado = TablaLibros.getSelectionModel().getSelectedItem();
        if (seleccionado != null && usuarioSesion != null) {
            boolean ok = dao.reservarLibro(seleccionado.getIsbn(), usuarioSesion);
            if (ok) {
                alertar("Reserva", "Libro reservado con éxito", Alert.AlertType.INFORMATION);
                cargarDatosIniciales();
            }
        }
    }

    private void manejarDevolucion() {
        Libro seleccionado = TablaLibros.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            boolean ok = dao.devolverLibro(seleccionado.getIsbn());
            if (ok) {
                alertar("Devolución", "Libro devuelto", Alert.AlertType.INFORMATION);
                cargarDatosIniciales();
            }
        }
    }

    private void configurarFiltros() {
        // Filtro por búsqueda de texto
        searchLibro.textProperty().addListener((obs, oldText, newText) -> {
            filtrarLista(newText, selectGenero.getValue());
        });

        // Filtro por ComboBox
        selectGenero.valueProperty().addListener((obs, oldVal, newVal) -> {
            filtrarLista(searchLibro.getText(), newVal);
        });
    }

    private void filtrarLista(String texto, String gen) {
        ObservableList<Libro> filtered = masterData.filtered(libro -> {
            boolean coincideTexto = texto == null || texto.isEmpty() || 
                             libro.getTitulo().toLowerCase().contains(texto.toLowerCase());
            boolean coincideGen = gen == null || libro.getGenero().equals(gen);
            return coincideTexto && coincideGen;
        });
        TablaLibros.setItems(filtered);
    }

    private void cerrarSesion() {
        // Lógica para volver a la ventana de Login
        System.out.println("Cerrando sesión...");
    }

    private void alertar(String titulo, String cont, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setContentText(cont);
        a.show();
    }
}