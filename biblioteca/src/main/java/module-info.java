module com.juan {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;

    // Para Hibernate y tus entidades
    opens com.juan.model to org.hibernate.orm.core;
    
    // Para el cargador de FXML (App principal)
    opens com.juan to javafx.fxml;
    opens com.juan.controlador.admin to javafx.fxml;
    opens com.juan.controlador.estudiante to javafx.fxml;
    opens com.juan.controlador.login_registro to javafx.fxml;

    exports com.juan;
    exports com.juan.controlador.admin;
    exports com.juan.controlador.login_registro;
}