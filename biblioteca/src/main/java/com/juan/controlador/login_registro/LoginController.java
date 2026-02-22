package com.juan.controlador.login_registro;

import com.juan.controlador.estudiante.EstudianteController;
import com.juan.dao.DaoBliblioteca;
import com.juan.dao.DaoBlibliotecaImpl;
import com.juan.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField showpasswordField;
    @FXML private CheckBox showPassword;
    @FXML private Button btnSesion;

    private DaoBliblioteca dao = new DaoBlibliotecaImpl();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Sincronización automática: lo que escribas en uno se escribe en el otro
        showpasswordField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    void mostrarPassword(MouseEvent event) {
        if (showPassword.isSelected()) {
            showpasswordField.setVisible(true);
            passwordField.setVisible(false);
        } else {
            passwordField.setVisible(true);
            showpasswordField.setVisible(false);
        }
    }

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
        
        if (usuarioLogueado != null) {
            String fxml = "";
            String titulo = "";

            if (usuarioLogueado.isAdmin()) {
                // Si tienes un AdminController, también deberías pasarle el usuario si lo necesita
                fxml = "/com/juan/component_admin/view_admin.fxml"; 
                titulo = "Panel de Administración - " + usuarioLogueado.getNombre();
            } else {
            
                EstudianteController.setUsuarioSesion(usuarioLogueado);
                
                fxml = "/com/juan/component_estudiante/view_estudiante.fxml"; 
                titulo = "Biblioteca - Estudiante: " + usuarioLogueado.getNombre();
            }

            navegarA(fxml, titulo);
            
        } else {
            mostrarAlerta("Error de datos", "No se pudo cargar el perfil.", Alert.AlertType.ERROR);
        }
    } else {
        mostrarAlerta("Error", "Usuario o contraseña incorrectos.", Alert.AlertType.ERROR);
    }
}

private void navegarA(String fxml, String titulo) {
    try {
        // 1. Cargar la nueva vista
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxml));
        javafx.scene.Parent root = loader.load();

        // 2. Crear el nuevo Stage (ventana)
        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.setTitle(titulo);
        stage.setScene(new javafx.scene.Scene(root));
        stage.show();

        // 3. Cerrar la ventana actual (Login)
        // Obtenemos el stage actual a través de cualquier componente (ej: emailField)
        javafx.stage.Stage currentStage = (javafx.stage.Stage) emailField.getScene().getWindow();
        currentStage.close();

    } catch (Exception e) {
        e.printStackTrace();
        mostrarAlerta("Error de Navegación", "No se pudo abrir la ventana: " + fxml, Alert.AlertType.ERROR);
    }
}

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}