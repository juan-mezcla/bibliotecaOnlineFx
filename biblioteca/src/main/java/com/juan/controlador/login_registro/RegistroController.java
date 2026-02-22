package com.juan.controlador.login_registro;

import java.net.URL; 
import java.util.ResourceBundle;
import com.juan.dao.DaoBliblioteca;
import com.juan.dao.DaoBlibliotecaImpl;
import com.juan.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable; 
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class RegistroController implements Initializable { 

    // 1. SEPARAR LOS CAMPOS POR TIPO:
    @FXML private TextField nombreField, apellidosField, userField, emailField;
    // ¡CAMBIO AQUÍ!: Declarar estos dos como PasswordField para que coincida con el FXML
    @FXML private PasswordField passwordField, confirmPasswordField; 
    
    @FXML private ChoiceBox<String> tipoUserSelect;
    @FXML private TextField showpassword, showpassword1;
    @FXML private CheckBox checkContrasena;

    private DaoBliblioteca dao = new DaoBlibliotecaImpl();

    @Override 
    public void initialize(URL location, ResourceBundle resources) {
        tipoUserSelect.getItems().addAll("Estudiante", "Admin");
        tipoUserSelect.setValue("Estudiante");
        
        // 2. ¡AÑADIR ESTO!: Sincronizar el texto entre los campos ocultos y los visibles
        showpassword1.textProperty().bindBidirectional(passwordField.textProperty());
        showpassword.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    }

    @FXML
    void mostrarPassword(MouseEvent event) {
        if (checkContrasena.isSelected()) {
            showpassword1.setVisible(true);
            showpassword.setVisible(true);
            passwordField.setVisible(false);
            confirmPasswordField.setVisible(false);
        } else {
            showpassword1.setVisible(false);
            showpassword.setVisible(false);
            passwordField.setVisible(true);
            confirmPasswordField.setVisible(true);
        }
    }

    @FXML
    void comprobarRegistro(MouseEvent event) {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            mostrarAlerta("Error", "Las contraseñas no coinciden.", Alert.AlertType.ERROR);
            return;
        }

        String seleccion = tipoUserSelect.getValue();
        boolean esAdmin = seleccion.equals("Admin");

        User nuevoUsuario = new User(
            userField.getText(),
            nombreField.getText(),
            apellidosField.getText(),
            emailField.getText(),
            passwordField.getText(),
            esAdmin
        );

        if (dao.insertarUsuario(nuevoUsuario)) {
            mostrarAlerta("Éxito", "Usuario " + nuevoUsuario.getUsuario() + " registrado.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudo guardar en la base de datos.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}