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

public class Login_registroController implements Initializable {

    @FXML
    private Button btnViewInicio;

    @FXML
    private Button btnViewRegistro;

    @FXML
    private Label lblTitulo;

    @FXML
    private StackPane panel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // CORREGIDO: Sin la carpeta /view/
        cargarSubVista("/com/juan/component_login/view_login.fxml");
    }

    @FXML
    void verInicioSesion(MouseEvent event) {
        // CORREGIDO: Sin la carpeta /view/
        cargarSubVista("/com/juan/component_login/view_login.fxml");
    }

    @FXML
    void verRegistroSesion(MouseEvent event) {
        // CORREGIDO: Sin la carpeta /view/
        cargarSubVista("/com/juan/component_registro/view_registro.fxml");
    }

    // Añade esto a Login_registroController.java
@FXML
void mostrarPassword(MouseEvent event) {
    // Tu lógica para mostrar contraseña
    System.out.println("Clic en mostrar password");
}

@FXML
void comprobarUsuario(MouseEvent event) {
    // Tu lógica de login
    System.out.println("Intentando iniciar sesión...");
}

    private void cargarSubVista(String fxmlPath) {
        try {
            panel.getChildren().clear();
            
            URL fxmlUrl = getClass().getResource(fxmlPath);
            
            // VALIDACIÓN: Evita que el programa explote si la ruta está mal
            if (fxmlUrl == null) {
                System.err.println("❌ ERROR: No se encontró el archivo exacto en: " + fxmlPath);
                return; 
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent vista = loader.load();
            panel.getChildren().add(vista);
            
        } catch (IOException e) {
            System.err.println("❌ Error al cargar la sub-vista: " + fxmlPath);
            e.printStackTrace();
        }
    }
}