package com;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * JavaFX App
 */
public class App extends Application {
    double mouseX;
    double mouseY;
    @FXML 
    static Button btn;
    private static Scene scene;
    DataBaseHandler db = new DataBaseHandler();
    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException, SQLException {
        FlowPane pane = new FlowPane();
       
        scene = new Scene(loadFXML("first_page"), 700, 480);
        
        
        stage.setScene(scene);
        stage.show();
        stage.setTitle("SQLite GUI");   
        db.openSQLFile("D://01WorkDeadlyLine//Java2022//sqlite-gui//items");
        showTables();
        System.out.println();


}
        
    public void showTables() throws ClassNotFoundException, SQLException {
        VBox tables = (VBox) scene.lookup("#tables");
        ResultSet rs = db.getTablesSet();
        while(rs.next()){
            if(!rs.getString(1).equals("sqlite_sequence")){
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
                items_column.setMaxWidth(Double.MAX_VALUE);
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
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}