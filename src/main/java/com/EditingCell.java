package com;

import java.sql.SQLException;

import com.dbase.DataBaseHandler;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class EditingCell extends TableCell<ObservableList<Object>, String> {
    private TextField textField;
    String startValue = null;
    static EditingCell editCell;
    EditingCell currentCell = this;
    @Override
    public void startEdit() {
        startValue = this.getText();
        super.startEdit();
        if (textField == null) {

            try {
                createTextField();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        setText(null);
        setGraphic(textField);
        textField.selectAll();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText((String) getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText("");
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                this.setOnMouseClicked(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent arg0) {
                        
                        if(editCell == null){
                            editCell = currentCell;
                            startEdit();
                        } else {
                            editCell.cancelEdit();
                            editCell = currentCell;
                            startEdit();
                            
                        }
                    }
                    
                });
                setGraphic(null);
            }
        }
    }

    private void createTextField() throws SQLException {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());

                    String field = null;
                    int row = 0;

                    field = MainPage.getTableView().getFocusModel().getFocusedCell().getTableColumn().getText();
                    row = MainPage.getTableView().getFocusModel().getFocusedCell().getRow() + 1;
                    System.out.println(field + " Начальное значение " + startValue);

                    try {
                        DataBaseHandler.updateValue(MainPage.getCurrentTable(), field, row,
                                textField.getText());
                    } catch (SQLException | ClassNotFoundException e) {

                        e.printStackTrace();
                    }

                    for (int i = 0; i < MainPage.data.size(); i++) {
                        // System.out.println(startValue);
                        for (int j = 0; j < MainPage.row.size(); j++) {
                            if (MainPage.data.get(i).get(j).equals(startValue)) {

                                MainPage.data.get(i).set(j, textField.getText());
                            }
                        }
                    }

                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            }
        });

        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                    Boolean newValue) {
                if (!newValue) {

                    commitEdit(textField.getText());
                }
            }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }

}