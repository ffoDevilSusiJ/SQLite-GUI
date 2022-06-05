package com;

import com.dialogs.AddItemDialog;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class Cleaner {

    static Scene mainPage = App.getScene();
    static Scene addItemDialog = AddItemDialog.getScene();

    
    public static void newItemList() {
        VBox gridContainer = (VBox) mainPage.lookup("#gridContainer");
        Node table = mainPage.lookup("#ListofItems");
        if (table != null) {
            gridContainer.getChildren().remove(table);
        }

    }

    public static void newTableList() {
        newItemList();
        VBox tables = (VBox) mainPage.lookup("#ListofTables");
        tables.getChildren().clear();
    }

    public static void clearAddItemDialog(){
        GridPane grid = (GridPane) addItemDialog.lookup("#GridOfValues");
        if (grid != null) {
            grid.getChildren().clear();
            grid.getColumnConstraints().removeAll(grid.getColumnConstraints());
            grid.getRowConstraints().removeAll(grid.getRowConstraints());
        }
    }
}
