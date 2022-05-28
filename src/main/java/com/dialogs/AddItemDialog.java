package com.dialogs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.MainPage;
import com.dbase.DataBaseHandler;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddItemDialog implements Dialogs {
    static Scene scene = null;
    static int finalWidth;
    static int finalHeight;
    
    @Override
    public void display() throws ClassNotFoundException, SQLException, IOException {
        finalWidth = 0;
        finalHeight = 0;
        DataBaseHandler.getPrimaryKeys(MainPage.getCurrentTable());
        ArrayList<String> values = new ArrayList<String>();
        scene = new Scene(Dialogs.loadFXML("add_item_dialog"), 0, 150);
        
        Stage stage = new Stage();
        
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox pane = (VBox) scene.lookup("#vbox");
        pane.setSpacing(30);
        
        GridPane grid = new GridPane();
        grid.setId("GridOfValues");
        pane.setPrefSize(250, 150);
        grid.getColumnConstraints().add(new ColumnConstraints(setWidth(80)));
        grid.getColumnConstraints().add(new ColumnConstraints(setWidth(80)));
        grid.getColumnConstraints().add(new ColumnConstraints(setWidth(220)));
        grid.setAlignment(Pos.CENTER);
        GridPane.setMargin(grid, new Insets(0, 0, 30, 0));

        GridPane buttons = new GridPane();
        buttons.getColumnConstraints().add(new ColumnConstraints(80));
        buttons.getColumnConstraints().add(new ColumnConstraints(80));

        buttons.setAlignment(Pos.CENTER);

        // 
        Label tip = new Label("The primary key should not be repeated");
        tip.getStyleClass().add("error");
        //

        Button submit = new Button("Submit");
        submit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

                try {
                    if (!grid.getChildren().contains(tip)) {
                        for (int i = 0; i < MainPage.columnNames.size(); i++) {
                            TextField textField = (TextField) scene.lookup("#" + "text-field_" + i);
                            values.add(textField.getText());
                        }
                        DataBaseHandler.addItem(MainPage.getCurrentTable(), values);
                        MainPage.buildData(MainPage.getCurrentTable());
                        stage.close();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }

        });
        Button cancel = new Button("Cancel");
        cancel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                stage.close();
            }

        });
        buttons.add(submit, 0, 0);
        buttons.add(cancel, 1, 0);
        for (int i = 0; i < MainPage.columnNames.size(); i++) {
            if (MainPage.columnNames.get(i)
                    .equals(DataBaseHandler.getPrimaryKeys(MainPage.getCurrentTable()).getString("COLUMN_NAME"))) {
                
                grid.getRowConstraints().add(new RowConstraints(setHeight(30)));
                grid.add(new Label(MainPage.columnNames.get(i)), 0, i);
                TextField textField = new TextField();
                textField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {

                        for (int i = 0; i < MainPage.data.size(); i++) {
                            if (newValue.equals(MainPage.data.get(i).get(0))) {
                                grid.add(tip, 2, 0);
                                break;
                            }
                            if (grid.getChildren().contains(tip)) {
                                grid.getChildren().remove(tip);
                            }

                        }
                    }
                });
                textField.setId("text-field_" + i);
                grid.add(textField, 1, i);

            } else {
                grid.getRowConstraints().add(new RowConstraints(setHeight(30)));
                grid.add(new Label(MainPage.columnNames.get(i)), 0, i);
                TextField textField = new TextField();
                textField.setId("text-field_" + i);
                grid.add(textField, 1, i);
            }
        }
        pane.getChildren().add(grid);
        pane.getChildren().add(buttons);
        stage.setWidth(finalWidth + 10);
        stage.setHeight(finalHeight + 100);
        stage.setTitle("Add new item");
        stage.setScene(scene);
        stage.showAndWait();
        DataBaseHandler.getTableItem(MainPage.getCurrentTable());
    }


    private static int setWidth(int i) {
        finalWidth = finalWidth + i;
        return i;
    }
    private static int setHeight(int i) {
        finalHeight= finalHeight + i;
        return i;
    }
    public static Scene getScene() {
        return scene;
    }

}
