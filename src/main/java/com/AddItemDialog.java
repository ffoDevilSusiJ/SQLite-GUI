package com;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddItemDialog {
    static Scene scene = null;
    static FXMLLoader fxmlLoader;

    public static void display() throws ClassNotFoundException, SQLException, IOException {
        DataBaseHandler.getPrimaryKeys(MainPage.currentTable);
        ArrayList<String> values = new ArrayList<String>();
        scene = new Scene(loadFXML("add_item_dialog"), 390, 150);

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox pane = (VBox) scene.lookup("#vbox");
        pane.setSpacing(30);

        GridPane grid = new GridPane();
        grid.setId("GridOfValues");
        pane.setPrefSize(250, 150);
        grid.getColumnConstraints().add(new ColumnConstraints(80));
        grid.getColumnConstraints().add(new ColumnConstraints(80));
        grid.getColumnConstraints().add(new ColumnConstraints(220));
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
                        DataBaseHandler.addItem(MainPage.currentTable, values);
                        MainPage.buildData(MainPage.currentTable);
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
                    .equals(DataBaseHandler.getPrimaryKeys(MainPage.currentTable).getString("COLUMN_NAME"))) {
                
                grid.getRowConstraints().add(new RowConstraints(30));
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
                grid.getRowConstraints().add(new RowConstraints(30));
                grid.add(new Label(MainPage.columnNames.get(i)), 0, i);
                TextField textField = new TextField();
                textField.setId("text-field_" + i);
                grid.add(textField, 1, i);
            }
        }
        pane.getChildren().add(grid);
        pane.getChildren().add(buttons);

        stage.setTitle("Add new item");
        stage.setScene(scene);
        stage.showAndWait();
        DataBaseHandler.getTableItem(MainPage.currentTable);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));

        return fxmlLoader.load();
    }

    static Scene getScene() {
        return scene;
    }

}
