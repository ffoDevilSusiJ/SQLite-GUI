module com.curves {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    opens com to javafx.fxml;
    exports com;
}
