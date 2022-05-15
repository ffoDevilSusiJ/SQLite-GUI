package com;

import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class Cleaner {

    static Scene scene = App.getScene();

    public static void newItemList() {
        VBox gridContainer = (VBox) scene.lookup("#gridContainer");
        TableView table = (TableView) scene.lookup("#ListofItems");
        if (table != null) {
            gridContainer.getChildren().remove(table);
        }
        // if (grid != null) {
        //     grid.getChildren().clear();
        //     grid.getColumnConstraints().removeAll(grid.getColumnConstraints());
        //     grid.getRowConstraints().removeAll(grid.getRowConstraints());
        // }
    }

    public static void newTableList() {
        newItemList();
        VBox tables = (VBox) scene.lookup("#ListofTables");
        tables.getChildren().clear();
    }
}
