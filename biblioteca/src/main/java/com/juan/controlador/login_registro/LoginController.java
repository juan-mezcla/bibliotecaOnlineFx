package com.juan.controlador.login_registro;

import com.juan.dao.DaoBliblioteca;
import com.juan.dao.DaoBlibliotecaImpl;
import com.juan.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TextField showpasswordField;
    @FXML private CheckBox showPassword;

    // Instanciamos el DAO
    private DaoBliblioteca dao = new DaoBlibliotecaImpl();

    @FXML
void comprobarUsuario(MouseEvent event) {
    String email = emailField.getText();
    String pass = passwordField.getText();

    if (email.isEmpty() || pass.isEmpty()) {
        mostrarAlerta("Campos vacíos", "Por favor, introduce tus credenciales.", Alert.AlertType.WARNING);
        return;
    }

    int idUser = dao.comprobarUsuario(email, pass);

    if (idUser != -1) {
        User usuarioLogueado = dao.seleccionarUser(idUser);
        
        // Usamos el booleano admin de tu modelo
        if (usuarioLogueado.isAdmin()) {
            System.out.println("Bienvenido Admin: " + usuarioLogueado.getNombre());
            // Lógica para cargar vista Admin
        } else {
            System.out.println("Bienvenido Estudiante: " + usuarioLogueado.getNombre());
            // Lógica para cargar vista Estudiante
        }
    } else {
        mostrarAlerta("Error", "Usuario o contraseña incorrectos.", Alert.AlertType.ERROR);
    }
}

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    


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
}