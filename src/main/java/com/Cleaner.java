package com;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class Cleaner {

    static Scene scene = App.getScene();
    public static void newItemList() {
        GridPane grid = (GridPane) scene.lookup("#ListofItems");
        grid.getChildren().clear();
        grid.getColumnConstraints().removeAll(grid.getColumnConstraints());
        grid.getRowConstraints().removeAll(grid.getRowConstraints());
    }

    public static void newTableList(){
        newItemList();
        VBox tables = (VBox) scene.lookup("#ListofTables");
        tables.getChildren().clear();
    }
}

