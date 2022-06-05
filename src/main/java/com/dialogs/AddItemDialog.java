package com.dialogs;

import java.io.IOException;
import java.sql.ResultSet;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddItemDialog implements Dialogs {
    static Scene scene = null;
    int row = 0;
    boolean primaryKeyRepeat = false;

    @Override
    public void display() throws ClassNotFoundException, SQLException, IOException {
        ResultSet primaryKeys = DataBaseHandler.getPrimaryKeys(MainPage.getCurrentTable());
        String primField = primaryKeys.getString("COLUMN_NAME");
        primaryKeys.getStatement().getConnection().close();
        ArrayList<String> values = new ArrayList<String>();
        scene = new Scene(Dialogs.loadFXML("add_item_dialog"), 0, 150);
        
        Stage stage = new Stage();

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox pane = (VBox) scene.lookup("#vbox");
        VBox gridVBox = new VBox();
        gridVBox.setPrefHeight(100);
        ScrollPane scroll = new ScrollPane(gridVBox) {
            public void requestFocus() {
            }
        };
        scroll.setFitToWidth(true);
        pane.setSpacing(30);

        pane.setPrefSize(250, 150);

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
                    if (!primaryKeyRepeat) {
                        
                        for (int i = 0; i < MainPage.columnNames.size(); i++) {
                            TextField textField = (TextField) scene.lookup("#text-field_" + i);
                            values.add(textField.getText());
                        }
                        DataBaseHandler.addItem(MainPage.getCurrentTable(), values);

                        stage.close();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    values.clear();
                    e.printStackTrace();
                }

            }

        });
        Button cancel = new Button("Cancel");
        cancel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                // try {
                //     primaryKeys.getStatement().getConnection().close();
                // } catch (SQLException e) {
                //     e.printStackTrace();
                // }
                stage.close();
            }

        });
        buttons.add(submit, 0, 0);
        buttons.add(cancel, 1, 0);

        next: for (int i = 0; i < MainPage.columnNames.size(); i++) {

            if (MainPage.columnNames.get(i)
                    .equals(primField)
                    && !(DataBaseHandler.getAutoincrement(MainPage.getCurrentTable(), (i + 1)))) {
                GridPane grid = new GridPane();

                grid.getColumnConstraints().add(new ColumnConstraints(80));
                grid.getColumnConstraints().add(new ColumnConstraints(80));
                grid.getColumnConstraints().add(new ColumnConstraints(220));
                grid.setAlignment(Pos.CENTER);
                GridPane.setMargin(grid, new Insets(0, 0, 30, 0));
                grid.getRowConstraints().add(new RowConstraints(30));
                grid.add(new Label(MainPage.columnNames.get(i)), 0, 0);
                TextField textField = new TextField();
                textField.textProperty().addListener(new ChangeListener<String>() {

                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
                        grid.getChildren().remove(tip);
                        for (int i = 0; i < MainPage.data.size(); i++) {
                            if (newValue.equals(MainPage.data.get(i)
                                    .get(MainPage.columnNames.indexOf(primField)))) {
                                grid.add(tip, 2, 0);
                                primaryKeyRepeat = true;
                                break;
                            } else {
                                primaryKeyRepeat = false;
                            }
                        }
                    }
                });
                textField.setId("text-field_" + i);
                grid.add(textField, 1, 0);
                gridVBox.getChildren().add(grid);
                row++;
            } else if (!MainPage.columnNames.get(i)
                    .equals(primField)) {
                GridPane grid = new GridPane();
                grid.getColumnConstraints().add(new ColumnConstraints(80));
                grid.getColumnConstraints().add(new ColumnConstraints(80));
                grid.getColumnConstraints().add(new ColumnConstraints(220));
                grid.setAlignment(Pos.CENTER);
                GridPane.setMargin(grid, new Insets(0, 0, 30, 0));
                grid.getRowConstraints().add(new RowConstraints(30));
                grid.add(new Label(MainPage.columnNames.get(i)), 0, 0);
                TextField textField = new TextField();
                textField.setId("text-field_" + i);
                grid.add(textField, 1, 0);
                gridVBox.getChildren().add(grid);
                row++;
            } else {
                TextField textField = new TextField("F");
                textField.setId("text-field_" + i);
                textField.setVisible(false);
                textField.setManaged(false);
                pane.getChildren().add(textField);
                int column = MainPage.columnNames.indexOf(primField);
                ArrayList<Integer> idList = new ArrayList<>();
                if(MainPage.data.size() > 0){
                int max = Integer.parseInt(MainPage.data.get( MainPage.data.size() - 1).get(column));
                for (int j = 0; j < MainPage.data.size(); j++) {
                    idList.add(Integer.parseInt(MainPage.data.get(j).get(column)));
                }
                for(int j = 0; j < idList.size(); j++){
                    if((j + 1) != idList.get(j)){
                        textField.setText(String.valueOf((j + 1)));
                        continue next;
                    }
                }
                textField.setText(String.valueOf((max + 1)));
                } else {
                    textField.setText(String.valueOf((1)));
                }
            }

        }

        pane.getChildren().add(scroll);
        pane.getChildren().add(buttons);
        stage.setWidth(400);
        stage.setHeight(250);
        stage.setTitle("Add new item");
        stage.setScene(scene);

        stage.showAndWait();

        // DataBaseHandler.getTableItem(MainPage.getCurrentTable());
    }

    public static Scene getScene() {
        return scene;
    }

}
