package com.juan.controlador.login_registro;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class RegistroController {

    @FXML private TextField nombreField;
    @FXML private TextField apellidosField;
    @FXML private TextField userField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TextField confirmPasswordField;
    @FXML private ChoiceBox<String> tipoUserSelect;
    @FXML private CheckBox checkContrasena;
    @FXML private Button btnRegistro;

    @FXML
    void mostrarPassword(MouseEvent event) {
        // Lógica similar a la de login para mostrar/ocultar
        System.out.println("Cambiando visibilidad de contraseña en registro");
    }

    @FXML
    void comprobarRegistro(MouseEvent event) {
        System.out.println("Registrando a: " + userField.getText());
        // Aquí validas que las contraseñas coincidan y guardas en BD
    }
}
