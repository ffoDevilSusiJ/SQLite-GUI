package com;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

public class PrimaryKeyCell extends TableCell<ObservableList<Object>, String>{
    
    private TextField textField;
    String startValue = null;

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
                getStyleClass().add("oneCell");
                
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
