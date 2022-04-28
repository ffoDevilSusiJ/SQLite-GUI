package com;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import java.awt.Desktop;

/**
 * JavaFX App
 */
public class App extends Application {
    private Desktop desktop = Desktop.getDesktop();
    Stage stage;
    private static Scene scene;
    DataBaseHandler db = new DataBaseHandler();
    static FXMLLoader fxmlLoader;
    @Override
    public void start(Stage main_stage) throws IOException, ClassNotFoundException, SQLException {
        FlowPane pane = new FlowPane();
        stage = main_stage;
        scene = new Scene(loadFXML("first_page"), 700, 480);

        
        stage.setScene(scene);
        stage.show();
        stage.setTitle("SQLite GUI");   
        
        
        System.out.println();
        FilePicker();
       
}
    public void FilePicker() {
        
        final FileChooser fileChooser = new FileChooser();
 
        MenuItem open_item = (MenuItem) fxmlLoader.getNamespace().get("file_open");
        VBox tables = (VBox) scene.lookup("#tables");
        open_item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("DataBase Files (*.db)", "*.db");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(stage);
                System.out.println();
                if (file != null) {
                    try {
                        db.openSQLFile(file.toPath().toString().replace("\\", "//"));
                        System.out.println(file.toPath().toString().replace("\\", "//"));
                        showTables();
                    } catch (SQLException | ClassNotFoundException ex2) {
                        ex2.printStackTrace();
                    }
                    
                }
            }

        });
    }
    public void clearWorkSpace() {
        VBox tables = (VBox) scene.lookup("#tables");
        tables.getChildren().clear();
        VBox items_row =  (VBox) scene.lookup("#tables_item");
        items_row.getChildren().clear();
    }
    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {

        }
    }
    public void showTables() throws ClassNotFoundException, SQLException {
        VBox tables = (VBox) scene.lookup("#tables");
        clearWorkSpace();
        ResultSet rs = db.getTablesSet();
       
        while(rs.next()){
            if(!rs.getString(1).equals("sqlite_sequence")){
                System.out.println(rs.getString(1));
                Label label = new Label(rs.getString(1));
                label.setId("table_id");
                label.setPrefHeight(40);
                label.setOnMouseClicked(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent arg0) {
                        try {
                            showActiveTableItems(label.getText());
                        } catch (ClassNotFoundException | SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    
                });
                tables.setMargin(label, new Insets(0,15,0,0));
                tables.getChildren().add(label);
            }
        }
    }
    public void showActiveTableItems(String table) throws ClassNotFoundException, SQLException {
        VBox items_row =  (VBox) scene.lookup("#tables_item");
        HBox tables_name = new HBox();
        items_row.getChildren().clear();
        items_row.getChildren().add(tables_name);
        ResultSet rs = db.getTableItem(table);
        int i = 1;
        while(rs.getMetaData().getColumnCount() >= i){
            
            Label label = new Label(rs.getMetaData().getColumnName(i));        
            label.setPrefHeight(40);
            tables_name.setMargin(label, new Insets(0,15,0,15));
            tables_name.getChildren().add(label);
            i++;
        }
        rs.close();
        rs = db.getTableItem(table);
        while(rs.next()){
            if(!rs.getString(1).equals("sqlite_sequence")){
                HBox items_column = new HBox();
                
                items_column.setId("items_row");
                Label label = new Label(rs.getString(2));        
                label.setPrefHeight(40);
                items_column.setMargin(label, new Insets(0,15,0,15));
                items_column.getChildren().add(label);
                items_row.getChildren().add(items_column);
            }
        }
        rs.close();
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