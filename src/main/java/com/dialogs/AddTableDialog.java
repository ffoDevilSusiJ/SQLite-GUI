package com.dialogs;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.MainPage;
import com.dbase.DataBaseField;
import com.dbase.DataBaseFieldTypes;
import com.dbase.DataBaseHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddTableDialog implements Dialogs {
    static Scene scene = null;
    int row = 0;
    static ArrayList<DataBaseField> fields;
    boolean primaryKeyRepeat = false;
    static VBox gridVBox;
    static int fieldCount;
    static Button addFieldButton = null;
    static String tableName; 
    @Override
    public void display() throws ClassNotFoundException, SQLException, IOException {
        
        fieldCount = 0;
        fields = new ArrayList<>();
        scene = new Scene(Dialogs.loadFXML("add_table_dialog"), 0, 150);

        Stage stage = new Stage();

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox pane = (VBox) scene.lookup("#vbox");
        gridVBox = new VBox();
        gridVBox.setPrefHeight(200);
        ScrollPane scroll = new ScrollPane(gridVBox) {
            public void requestFocus() {
            }
        };
        scroll.setFitToWidth(true);
        pane.setSpacing(10);

        pane.setPrefSize(250, 250);
        
        HBox tableNameArea = new HBox(10);
        tableNameArea.setAlignment(Pos.CENTER);
        TextField tableNameField = new TextField();
        

        tableNameField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String newValue) {
                tableName = newValue;
            }

        });
        Label nameLabel = new Label("Table Name");
        tableNameArea.getChildren().add(nameLabel);
        tableNameArea.getChildren().add(tableNameField);

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
                    DataBaseHandler.createTable(tableName, fields);
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
                stage.close();
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

        addField();
        pane.getChildren().add(tableNameArea);
        pane.getChildren().add(scroll);
        pane.getChildren().add(buttons);
        stage.setWidth(600);
        stage.setHeight(325);
        stage.setTitle("Add new item");
        stage.setScene(scene);

        stage.showAndWait();

        // DataBaseHandler.getTableItem(MainPage.getCurrentTable());
    }

    private static void addField() {
        DataBaseField field = new DataBaseField();
        fieldCount++;
        GridPane grid = new GridPane();
        grid.setId("field_" + fieldCount);
        grid.getColumnConstraints().add(new ColumnConstraints(110));
        grid.getColumnConstraints().add(new ColumnConstraints(150));
        grid.getColumnConstraints().add(new ColumnConstraints(30));
        grid.getColumnConstraints().add(new ColumnConstraints(30));
        grid.getColumnConstraints().add(new ColumnConstraints(30));
        grid.getColumnConstraints().add(new ColumnConstraints(30));
        grid.getColumnConstraints().add(new ColumnConstraints(30));
        grid.getColumnConstraints().add(new ColumnConstraints(30));
        grid.getColumnConstraints().add(new ColumnConstraints(30));
        grid.getColumnConstraints().add(new ColumnConstraints(30));
        grid.getColumnConstraints().add(new ColumnConstraints(30));
        grid.getRowConstraints().add(new RowConstraints(30));
        grid.setAlignment(Pos.CENTER);

        TextField fieldName = new TextField();
        fieldName.setPromptText("Field name");
        fieldName.setMaxWidth(80);
        fieldName.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String newValue) {
                field.setName(newValue);
            }

        });
        grid.add(fieldName, 0, 0);
        fieldName.setFocusTraversable(false);


        ObservableList<String> types = FXCollections.observableArrayList(DataBaseFieldTypes.getNames());
        ComboBox<String> boxs = new ComboBox<>(types);
        boxs.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                field.setType(boxs.getSelectionModel().getSelectedItem());
                System.out.println(field.getType());
            }
            
        });
        grid.add(boxs, 1, 0);


        grid.add(new Label("PK"), 2, 0);
        CheckBox pkBox = new CheckBox();
        pkBox.setFocusTraversable(false);
        pkBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if (field.isPrimaryKey())
                    field.setPrimaryKey(false);
                else
                    field.setPrimaryKey(true);
            }

        });

        grid.add(pkBox, 3, 0);

        grid.add(new Label("AI"), 4, 0);
        CheckBox aiBox = new CheckBox();
        aiBox.setFocusTraversable(false);
        aiBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if (field.isAutoIncrement())
                    field.setAutoIncrement(false);
                else
                    field.setAutoIncrement(true);

            }

        });
        grid.add(aiBox, 5, 0);

        grid.add(new Label("NN"), 6, 0);
        CheckBox nnBox = new CheckBox();
        nnBox.setFocusTraversable(false);
        nnBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if (field.isNOTNULL())
                    field.setNOTNULL(false);
                else
                    field.setNOTNULL(true);

            }

        });
        grid.add(nnBox, 7, 0);

        grid.add(new Label("DF"), 8, 0);
        CheckBox dfBox = new CheckBox();
        dfBox.setFocusTraversable(false);
        dfBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if (field.isHasDEFAULT())
                    field.setHasDEFAULT(false);
                else
                    field.setHasDEFAULT(true);
            }

        });
        grid.add(dfBox, 9, 0);

        grid.add(new Label("UN"), 10, 0);
        CheckBox unBox = new CheckBox();
        unBox.setFocusTraversable(false);
        unBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if (field.isUNIQUE())
                    field.setUNIQUE(false);
                else
                    field.setUNIQUE(true);
            }

        });
        grid.add(unBox, 11, 0);

        TextField textField = new TextField();
        textField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {

            }
        });
        for (int i = 0; i < fieldCount; i++) {
            GridPane pane = (GridPane) scene.lookup("#field_" + (i + 1));
            if (pane != null) {
                if (pane.getChildren().contains(addFieldButton)) {
                    pane.getChildren().remove(addFieldButton);
                    pane.getRowConstraints().remove(1);
                }
            }
        }
        grid.getRowConstraints().add(new RowConstraints(30));

        Button button = new Button("+");
        addFieldButton = button;
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                addField();
            }

        });
        fields.add(field);
        GridPane.setMargin(grid, new Insets(30, 30, 30, 30));
        grid.add(button, 0, 1);
        gridVBox.getChildren().add(grid);

    }

    public static Scene getScene() {
        return scene;
    }

}
