package com.juan.controlador.login_registro;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * Controlador principal de la pantalla de acceso.
 * Gestiona el intercambio de "sub-vistas" (Login y Registro) dentro de un StackPane.
 */
public class Login_registroController implements Initializable {

    // Botón para navegar hacia la vista de Inicio de Sesión
    @FXML
    private Button btnViewInicio;

    // Botón para navegar hacia la vista de Registro de nuevo usuario
    @FXML
    private Button btnViewRegistro;

    @FXML
    private Label lblTitulo;

    /**
     * El StackPane es el contenedor donde se inyectarán las sub-vistas FXML.
     * Funciona como una "pila", pero aquí lo usamos para mostrar un solo panel cada vez.
     */
    @FXML
    private StackPane panel;

    /**
     * Método de inicialización (se ejecuta al abrir la ventana).
     * Por defecto, cargamos la sub-vista de Login para que el usuario la vea nada más entrar.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarSubVista("/com/juan/component_login/view_login.fxml");
    }

    /**
     * Evento al hacer clic en el botón de "Iniciar Sesión".
     */
    @FXML
    void verInicioSesion(MouseEvent event) {
        cargarSubVista("/com/juan/component_login/view_login.fxml");
    }

    /**
     * Evento al hacer clic en el botón de "Registrarse".
     */
    @FXML
    void verRegistroSesion(MouseEvent event) {
        cargarSubVista("/com/juan/component_registro/view_registro.fxml");
    }
    
    /**
     * MÉTODO CLAVE: Gestiona la carga dinámica de archivos FXML.
     * @param fxmlPath Ruta relativa del archivo .fxml que queremos mostrar.
     */
    private void cargarSubVista(String fxmlPath) {
        try {
            // 1. Limpiamos el contenedor para eliminar la vista anterior
            panel.getChildren().clear();
            
            // 2. Localizamos el archivo FXML
            URL fxmlUrl = getClass().getResource(fxmlPath);
            
            // 3. VALIDACIÓN: Evita que el programa explote si la ruta está mal escrita o falta el archivo
            if (fxmlUrl == null) {
                System.err.println("ERROR: No se encontró el archivo exacto en: " + fxmlPath);
                return; 
            }
            
            // 4. Cargamos la jerarquía de nodos del FXML (el Parent)
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent vista = loader.load();
            
            // 5. Inyectamos la nueva vista dentro de nuestro StackPane principal
            panel.getChildren().add(vista);
            
        } catch (IOException e) {
            // Manejo de errores en la lectura del archivo o errores dentro del controlador de la sub-vista
            System.err.println("Error al cargar la sub-vista: " + fxmlPath);
            e.printStackTrace();
        }
    }
}