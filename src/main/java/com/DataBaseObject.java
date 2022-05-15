package com;

import javafx.beans.property.SimpleStringProperty;

public class DataBaseObject {
    private final SimpleStringProperty idColumnProperty = new SimpleStringProperty("");
    public String value;
    
    public DataBaseObject(String id, String i){
        value = i;
        setIdColumn(id);    
    }
    


    public String getIdColumn(){
        return idColumnProperty.get();
    }

    public void setIdColumn(String id){
        idColumnProperty.set(id);
    }
    public String getValue() {
        return value;
    }

}

