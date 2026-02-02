module com.juan {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.juan to javafx.fxml;
    exports com.juan;
}
