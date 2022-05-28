package com.dialogs;

import java.io.IOException;
import java.sql.SQLException;

import com.App;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public interface Dialogs {
    
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public void display() throws ClassNotFoundException, SQLException, IOException;
}
