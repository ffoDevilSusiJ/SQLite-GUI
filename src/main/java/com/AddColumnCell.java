package com;

import java.io.IOException;
import java.sql.SQLException;

import com.dialogs.AddFieldDialog;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class AddColumnCell extends TableCell<ObservableList<Object>, String>{
    
    
    String startValue = null;


    @Override
    public void updateItem(String item, boolean empty) {
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                try {
                    new AddFieldDialog().display();
                } catch (ClassNotFoundException | SQLException | IOException e) {
                    e.printStackTrace();
                } 
            }
            
        });
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        }
        // } else {
        //     if (isEditing()) {
        //         if (label != null) {
        //             label.setText("");
        //         }
        //         setText(null);
        //         setGraphic(label);
        //     } else {
        //         getStyleClass().add("oneCell");
                
        //         setText(getString());
        //         setGraphic(label);
        //     }
        // }
    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}