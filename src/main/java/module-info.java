module com.curves {
    requires javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires java.desktop;
    opens com to javafx.fxml;
    exports com;
}
