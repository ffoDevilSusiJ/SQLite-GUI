module com.curves {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens com to javafx.fxml;
    exports com;
}
