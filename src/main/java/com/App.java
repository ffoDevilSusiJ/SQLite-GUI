package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import javafx.scene.layout.AnchorPane;
/**
 * JavaFX App
 */
public class App extends Application {
    static Stage stage;  
    private static Scene scene;
    static FXMLLoader fxmlLoader;

    @Override
    public void start(Stage main_stage) throws IOException, ClassNotFoundException, SQLException {
        stage = main_stage;
        scene = new Scene(loadFXML("first_page"), 700, 480);

        stage.setScene(scene);
        stage.show();
        stage.setTitle("SQLite GUI");
        MainPage.showStartTipLabel();
        AnchorPane.setRightAnchor((HBox)scene.lookup("#topBox"),10.0);
        System.out.println();
        MainPage.FilePicker(fxmlLoader);
        

    }

    static Scene getScene(){
        return scene;
    } 
    static Stage getStage(){
        return stage;
    } 

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));

        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}