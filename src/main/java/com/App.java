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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        GridPane grid = (GridPane) scene.lookup("#items");
        grid.getChildren().clear();
        grid.getColumnConstraints().removeAll(grid.getColumnConstraints());
    }
    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {

        }
    }

    //List of tables on the right
    public void showTables() throws ClassNotFoundException, SQLException {
        VBox tables = (VBox) scene.lookup("#tables");
        ResultSet rs = db.getTablesSet();
        ArrayList<Label> tablesList = new ArrayList<Label>();
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
                            for (Label item : tablesList) {
                                if(item.getStyleClass().contains("active_table")){
                                    item.getStyleClass().remove("active_table");
                                }
                            }
                            label.getStyleClass().add("active_table");
                            showActiveTableItems(label.getText());
                        } catch (ClassNotFoundException | SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    
                });
                tables.setMargin(label, new Insets(0,15,0,0));
                tablesList.add(label);
                
            }
        }
        tables.getChildren().addAll(tablesList);
    }
    
    //Contents of the active table
    public void showActiveTableItems(String table) throws ClassNotFoundException, SQLException {
        ResultSet rs = db.getTableItem(table);
        GridPane grid = (GridPane) scene.lookup("#items");
        grid.setPadding(new Insets(0,0,0,20));
        
        int i = 1;
        clearWorkSpace();
        
        System.out.println(grid.getColumnCount());
        while(rs.getMetaData().getColumnCount() >= i){
            
            Label label = new Label(rs.getMetaData().getColumnName(i));        
            label.setPrefHeight(40);
            grid.getColumnConstraints().add(new ColumnConstraints(80));
            grid.add(label, i-1, 0);
            i++;
        }
        rs.close();
        rs = db.getTableItem(table);
        i = 0;
        int j = 1;
        while(rs.next()){
            
            if(!rs.getString(1).equals("sqlite_sequence")){
                i++;
                for(int k = 0; k < rs.getMetaData().getColumnCount(); k++){
                    Label label = new Label(rs.getString(k+1));
                    grid.add(label, k, j);
                    label.setPrefHeight(40);
                }

                grid.getRowConstraints().add(new RowConstraints(80));
                j++;
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