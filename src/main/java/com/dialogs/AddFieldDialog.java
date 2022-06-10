package com.dialogs;

import java.io.IOException;
import java.sql.SQLException;

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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddFieldDialog implements Dialogs {
    static Scene scene = null;
    static VBox pane;
    static Stage stage;
    static DataBaseField field;

    static TextField fieldNameTextField;
    static ComboBox comboBox;

    static boolean hasType = false;
    static boolean hasTableName = false;
    static boolean hasFieldName = false;
    static boolean hasPrimaryKey = false;
    static boolean hasNoRepeatFieldName = false;
    static boolean hasIntegerOnAutoincrement = false;

    @Override
    public void display() throws ClassNotFoundException, SQLException, IOException {
        scene = new Scene(Dialogs.loadFXML("add_item_dialog"), 0, 150);

        stage = new Stage();

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        pane = (VBox) scene.lookup("#vbox");
        pane.setSpacing(5);
        addField();
        addButtons();

        stage.setWidth(600);
        stage.setHeight(100);
        stage.setTitle("Add new item");
        stage.setScene(scene);
        stage.showAndWait();

    }

    private static void addButtons() {
        String currentTable = MainPage.getCurrentTable();
        GridPane buttons = new GridPane();
        buttons.getColumnConstraints().add(new ColumnConstraints(80));

        buttons.setAlignment(Pos.CENTER);

        Button submit = new Button("Submit");
        submit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

                for (String columnName : MainPage.getColumnNames()) {
                    if (columnName.equals(fieldNameTextField.getText())) {
                        hasNoRepeatFieldName = false;
                        if (!fieldNameTextField.getStyleClass().contains("error-box"))
                            fieldNameTextField.getStyleClass().add("error-box");
                    }
                }

                if (comboBox.getValue() == null) {
                    hasType = false;
                    if (!comboBox.getStyleClass().contains("error-box"))
                        comboBox.getStyleClass().add("error-box");
                }

                if (fieldNameTextField.getText() == "") {
                    hasFieldName = false;
                    if (!fieldNameTextField.getStyleClass().contains("error-box"))
                        fieldNameTextField.getStyleClass().add("error-box");
                }

                if (hasType && hasFieldName && hasNoRepeatFieldName) {
                    try {
                        DataBaseHandler.addField(currentTable, field);
                        MainPage.buildData(currentTable);
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                    }
                    stage.close();
                } else {
                    if (!hasType || !hasFieldName || !hasNoRepeatFieldName) {
                        String error = "";

                        if (!hasType) {
                            error = error + "The field type cannot be empty\n";
                        }
                        if (!hasFieldName) {
                            error = error + "The field name cannot be empty\n";
                        }
                        if (!hasNoRepeatFieldName) {
                            error = error + "Field names should not be duplicated\n";
                        }
                        Alert alert = new Alert(AlertType.ERROR, error,
                                ButtonType.OK);
                        alert.showAndWait();
                    }
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

        pane.getChildren().add(buttons);
    }

    private static void addField() {
        field = new DataBaseField();
        GridPane grid = new GridPane();

        grid.getColumnConstraints().add(new ColumnConstraints(110));
        grid.getColumnConstraints().add(new ColumnConstraints(150));
        int attr = 7;
        for (int i = 0; i < attr; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(30));
        }
        grid.getRowConstraints().add(new RowConstraints(30));
        grid.setAlignment(Pos.CENTER);

        TextField fieldName = new TextField();
        fieldNameTextField = fieldName;
        fieldName.setPromptText("Field name");
        fieldName.setMaxWidth(80);
        fieldName.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String newValue) {
                field.setName(newValue);
                fieldName.getStyleClass().remove("error-box");
                hasFieldName = true;
                hasNoRepeatFieldName = true;
            }

        });
        grid.add(fieldName, 0, 0);
        fieldName.setFocusTraversable(false);

        ObservableList<String> types = FXCollections.observableArrayList(DataBaseFieldTypes.getNames());
        ComboBox<String> boxs = new ComboBox<>(types);
        comboBox = boxs;
        boxs.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                field.setType(boxs.getSelectionModel().getSelectedItem());
                boxs.getStyleClass().remove("error-box");
                hasType = true;
                System.out.println(field.getType());
            }

        });
        grid.add(boxs, 1, 0);

        CheckBox aiBox = new CheckBox();
        aiBox.setDisable(true);
        CheckBox pkBox = new CheckBox();
        if (hasPrimaryKey)
            pkBox.setDisable(true);
        CheckBox nnBox = new CheckBox();

        // Not Null
        grid.add(new Label("NN"), 2, 0);
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
        grid.add(nnBox, 3, 0);

        grid.add(new Label("DF"), 4, 0);
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
        grid.add(dfBox, 5, 0);

        grid.add(new Label("UN"), 6, 0);
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
        grid.add(unBox, 7, 0);

        TextField textField = new TextField();
        textField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {

            }
        });
        pane.getChildren().add(grid);

    }

}
