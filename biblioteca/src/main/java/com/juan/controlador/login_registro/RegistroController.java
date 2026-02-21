package com.juan.controlador.login_registro;

import java.net.URL; // Importa solo una vez
import java.util.ResourceBundle;
import com.juan.dao.DaoBliblioteca;
import com.juan.dao.DaoBlibliotecaImpl;
import com.juan.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable; 
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class RegistroController implements Initializable { 

    @FXML private TextField nombreField, apellidosField, userField, emailField, passwordField, confirmPasswordField;
    @FXML private ChoiceBox<String> tipoUserSelect;

    private DaoBliblioteca dao = new DaoBlibliotecaImpl();

    @Override 
    public void initialize(URL location, ResourceBundle resources) {
        tipoUserSelect.getItems().addAll("Estudiante", "Admin");
        tipoUserSelect.setValue("Estudiante");
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