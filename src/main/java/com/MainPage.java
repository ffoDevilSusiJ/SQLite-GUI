package com;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainPage {
    static Scene scene = App.getScene();
    static Stage stage = App.getStage();


    //show start tip label
    public static void showStartTipLabel() {
        VBox gridContainer = (VBox) scene.lookup("#gridContainer");
        gridContainer.setAlignment(Pos.CENTER);
        Label label = new Label("To get started, open or create an SQLite database using (file -> New/Open)");
        label.getStyleClass().add("start_label");
        gridContainer.getChildren().add(label);
    }
    //hide start tip label
    public static void hideStartTipLabel() {
        VBox gridContainer = (VBox) scene.lookup("#gridContainer");
        gridContainer.getChildren().clear();
        gridContainer.setAlignment(Pos.TOP_LEFT);
    }

    // File Chooser
    public static void FilePicker(FXMLLoader fxmlLoader) {

        final FileChooser fileChooser = new FileChooser();

        MenuItem openMenuItem = (MenuItem) fxmlLoader.getNamespace().get("file_open");
        openMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("DataBase Files (*.db)",
                        "*.db");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(stage);
                System.out.println();
                if (file != null) {
                    try {
                        hideStartTipLabel();
                        DataBaseHandler.openSQLFile(file.toPath().toString().replace("\\", "//"));
                        Cleaner.newTableList();
                        showTables();
                    } catch (SQLException | ClassNotFoundException ex2) {
                        ex2.printStackTrace();
                    }

                }
            }

        });
    }
    //Create List of Items grid from active table
    public static void createGridPane() {
        if (scene.lookup("#ListofItems") == null) {
            VBox gridContainer = (VBox) scene.lookup("#gridContainer");
            GridPane grid = new GridPane();
            grid.setId("ListofItems");
            grid.getStyleClass().add("grid_style");
            gridContainer.getChildren().add(grid);
        }
    }

    // List of tables on the right
    public static void showTables() throws ClassNotFoundException, SQLException {
        VBox tables = (VBox) scene.lookup("#ListofTables");

        ResultSet rs = DataBaseHandler.getTablesSet();
        ArrayList<Label> tablesList = new ArrayList<Label>();
        while (rs.next()) {
            if (!rs.getString(1).equals("sqlite_sequence")) {
                System.out.println(rs.getString(1));
                Label label = new Label(rs.getString(1));
                label.setId("table_id");
                label.setPrefHeight(40);
                label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent arg0) {
                        try {
                            for (Label item : tablesList) {
                                if (item.getStyleClass().contains("active_table")) {
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
                VBox.setMargin(label, new Insets(0, 0, 0, 15));
                tablesList.add(label);

            }
        }
        tables.getChildren().addAll(tablesList);
    }

    // List of items from the active table
    public static void showActiveTableItems(String table) throws ClassNotFoundException, SQLException {
        createGridPane();
        ResultSet rs = DataBaseHandler.getTableItem(table);
        GridPane grid = (GridPane) scene.lookup("#ListofItems");
        grid.setPadding(new Insets(0, 0, 0, 20));

        int i = 1;
        Cleaner.newItemList();

       
        while (rs.getMetaData().getColumnCount() >= i) {

            Label label = new Label(rs.getMetaData().getColumnName(i));
            label.setPrefHeight(40);
            grid.getColumnConstraints().add(new ColumnConstraints(80));
            grid.add(label, i - 1, 0);
            i++;
        }
        
        rs.close();
        rs = DataBaseHandler.getTableItem(table);
        i = 0;
        int j = 1;
        while (rs.next()) {

            if (!rs.getString(1).equals("sqlite_sequence")) {
                i++;
                for (int k = 0; k < rs.getMetaData().getColumnCount(); k++) {
                    Label label = new Label(rs.getString(k + 1));
                    grid.add(label, k, j);
                    label.setPrefHeight(40);
                }

                grid.getRowConstraints().add(new RowConstraints(80));
                j++;
            }
        }
        rs.close();
    }

}
