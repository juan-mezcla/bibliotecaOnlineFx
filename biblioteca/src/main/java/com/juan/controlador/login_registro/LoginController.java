package com.juan.controlador.login_registro;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TextField showpasswordField;
    @FXML private CheckBox showPassword;
    @FXML private Button btnSesion;

    @FXML
    void mostrarPassword(MouseEvent event) {
        if (showPassword.isSelected()) {
            showpasswordField.setText(passwordField.getText());
            showpasswordField.setVisible(true);
            passwordField.setVisible(false);
        } else {
            passwordField.setText(showpasswordField.getText());
            passwordField.setVisible(true);
            showpasswordField.setVisible(false);
        }
    }

    @FXML
    void comprobarUsuario(MouseEvent event) {
        String email = emailField.getText();
        String pass = passwordField.getText();
        System.out.println("Intentando login con: " + email);
        // Aquí irá tu lógica de Hibernate/DAO
    }
}