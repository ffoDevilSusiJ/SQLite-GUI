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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
    static HBox addFieldBox = null;
    static String tableName;

    static ArrayList<ComboBox> comboBoxes;
    static ArrayList<TextField> fieldNameTextFields;
    static TextField tableNameTextField;
    static ArrayList<CheckBox> primaryKeyCheckBoxs;
    static ArrayList<CheckBox> autoincrementCheckBoxs;

    static boolean hasType;
    static boolean hasTableName;
    static boolean hasFieldName;
    static boolean hasPrimaryKey;
    static boolean hasNoRepeatFieldName;
    static boolean hasIntegerOnAutoincrement;

    static VBox pane;
    static Stage stage;

    @Override
    public void display() throws ClassNotFoundException, SQLException, IOException {
        hasType = false;
        hasTableName = false;
        hasFieldName = false;
        hasPrimaryKey = false;
        hasNoRepeatFieldName = false;
        hasIntegerOnAutoincrement = false;
        fieldCount = 0;
        fields = new ArrayList<>();
        scene = new Scene(Dialogs.loadFXML("add_table_dialog"), 0, 150);

        stage = new Stage();

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        pane = (VBox) scene.lookup("#vbox");
        gridVBox = new VBox();
        gridVBox.setSpacing(5);
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
        tableNameTextField = new TextField();

        tableNameTextField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String newValue) {
                hasTableName = true;
                tableNameTextField.getStyleClass().remove("error-box");
                tableName = newValue;
            }

        });
        tableNameArea.getChildren().add(new Label("Table Name"));
        tableNameArea.getChildren().add(tableNameTextField);

        comboBoxes = new ArrayList<>();
        fieldNameTextFields = new ArrayList<>();
        primaryKeyCheckBoxs = new ArrayList<>();
        autoincrementCheckBoxs = new ArrayList<>();

        addField();
        pane.getChildren().add(tableNameArea);
        pane.getChildren().add(scroll);
        addButtons();
        stage.setWidth(600);
        stage.setHeight(325);
        stage.setTitle("Add new item");
        stage.setScene(scene);

        stage.showAndWait();

        // DataBaseHandler.getTableItem(MainPage.getCurrentTable());
    }

    private static void addButtons() {
        GridPane buttons = new GridPane();
        buttons.getColumnConstraints().add(new ColumnConstraints(80));

        buttons.setAlignment(Pos.CENTER);

        Button submit = new Button("Submit");
        submit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

                for (TextField field : fieldNameTextFields) {
                    int repeat = 0;
                    for (int i = 0; i < fieldNameTextFields.size(); i++) {
                        if (field.getText().equals(fieldNameTextFields.get(i).getText())) {
                            repeat++;
                            if (repeat > 1) {
                                hasNoRepeatFieldName = false;
                                if (!field.getStyleClass().contains("error-box"))
                                    field.getStyleClass().add("error-box");
                                if (!fieldNameTextFields.get(i).getStyleClass().contains("error-box"))
                                    fieldNameTextFields.get(i).getStyleClass().add("error-box");
                            }
                        }

                    }
                }

                for (ComboBox comboBox : comboBoxes) {
                    if (comboBox.getValue() == null) {
                        hasType = false;
                        if (!comboBox.getStyleClass().contains("error-box"))
                            comboBox.getStyleClass().add("error-box");
                    }
                }

                for (TextField textField : fieldNameTextFields) {
                    if (textField.getText() == "") {
                        hasFieldName = false;
                        if (!textField.getStyleClass().contains("error-box"))
                            textField.getStyleClass().add("error-box");
                    }
                }

                if (tableNameTextField.getText() == "") {
                    hasTableName = false;
                    if (!tableNameTextField.getStyleClass().contains("error-box"))
                        tableNameTextField.getStyleClass().add("error-box");
                }

                if (!hasPrimaryKey) {
                    for (CheckBox box : primaryKeyCheckBoxs) {
                        if (!box.getStyleClass().contains("error-box"))
                            box.getStyleClass().add("error-box");
                    }
                }

                if (hasType && hasFieldName && hasTableName && hasPrimaryKey && hasNoRepeatFieldName) {
                    try {
                        DataBaseHandler.createTable(tableName, fields);
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                    }
                    stage.close();
                } else {
                    if (!hasType || !hasFieldName || !hasTableName || !hasPrimaryKey || !hasNoRepeatFieldName) {
                        String error = "";
                        if (!hasType) {
                            error = error + "The field type cannot be empty\n";
                        }
                        if (!hasFieldName) {
                            error = error + "The field name cannot be empty\n";
                        }
                        if (!hasTableName) {
                            error = error + "The table name cannot be empty\n";
                        }
                        if (!hasPrimaryKey) {
                            error = error + "There must be at least one primary key field\n";
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
        DataBaseField field = new DataBaseField();
        fieldCount++;
        GridPane grid = new GridPane();
        TextField fieldName = new TextField();
        ComboBox<String> boxs = new ComboBox<>();
        //Right click context menu
        grid.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent e) {
                if (e.getButton().equals(MouseButton.SECONDARY)) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem item1 = new MenuItem("delete");
                    item1.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent arg0) {
                            hasType = true;
                            hasFieldName = true;
                            hasNoRepeatFieldName = true;
                            fieldNameTextFields.remove(fieldName);
                            comboBoxes.remove(boxs);
                            fields.remove(field);
                            gridVBox.getChildren().remove(grid);
                        }
                        
                    });
                    contextMenu.getItems().add(item1);
                    contextMenu.setAutoHide(true);
                    contextMenu.show(scene.getWindow(), e.getScreenX(), e.getScreenY());
                    if(contextMenu.isShowing()){
                        
                    }
                }

            }

        });
        grid.setId("field_" + fieldCount);
        grid.getColumnConstraints().add(new ColumnConstraints(110));
        grid.getColumnConstraints().add(new ColumnConstraints(150));
        int attr = 9;
        for (int i = 0; i < attr; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(30));
        }
        grid.getRowConstraints().add(new RowConstraints(30));
        grid.setAlignment(Pos.CENTER);

       
        fieldName.setPromptText("Field name");
        fieldName.setMaxWidth(80);
        fieldNameTextFields.add(fieldName);
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
        boxs.getItems().addAll(types);

        comboBoxes.add(boxs);
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

        // Primary Key
        grid.add(new Label("PK"), 2, 0);
        primaryKeyCheckBoxs.add(pkBox);
        pkBox.setFocusTraversable(false);

        pkBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if (field.isPrimaryKey()) {
                    hasPrimaryKey = false;
                    field.setPrimaryKey(false);
                    for (CheckBox box : primaryKeyCheckBoxs) {
                        box.setDisable(false);
                    }
                    aiBox.setDisable(true);
                    boxs.setDisable(false);
                    aiBox.setSelected(false);
                    field.setAutoIncrement(false);
                } else {
                    hasPrimaryKey = true;
                    field.setPrimaryKey(true);
                    for (CheckBox box : primaryKeyCheckBoxs) {
                        aiBox.setDisable(false);
                        box.getStyleClass().remove("error-box");
                        box.setDisable(true);
                        pkBox.setDisable(false);
                    }
                }
            }

        });

        grid.add(pkBox, 3, 0);

        // Autoincrement
        grid.add(new Label("AI"), 4, 0);
        aiBox.setFocusTraversable(false);
        aiBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if (field.isAutoIncrement()) {
                    field.setAutoIncrement(false);
                    boxs.setDisable(false);
                } else {
                    boxs.setValue("INTEGER");
                    boxs.setDisable(true);
                    field.setAutoIncrement(true);
                }

            }

        });
        grid.add(aiBox, 5, 0);

        // Not Null
        grid.add(new Label("NN"), 6, 0);
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
                if (pane.getChildren().contains(addFieldBox)) {
                    pane.getChildren().remove(addFieldBox);
                    pane.getRowConstraints().remove(1);
                }
            }
        }
        fields.add(field);
        gridVBox.getChildren().add(grid);
        addFieldButton();

    }
    private static void addFieldButton(){
        HBox btnBox = new HBox();
        btnBox.setPadding(new Insets(0,0,0,18));
        Button button = new Button("+");
        if(addFieldBox != null){
            gridVBox.getChildren().remove(addFieldBox);
        }
        addFieldBox = btnBox;
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                addField();
            }

        });
        btnBox.getChildren().add(button);

        gridVBox.getChildren().add(btnBox);
    }
    public static Scene getScene() {
        return scene;
    }

}
