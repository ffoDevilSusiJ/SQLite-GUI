package com.dbase;

public class DataBaseField {
    String name;
    String type;
    boolean isAutoIncrement = false;
    boolean isPrimaryKey = false;
    boolean isUNIQUE = false;
    boolean isNOTNULL = false;
    boolean hasDEFAULT = false;
    boolean hasCHECK = false;

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
    public boolean isHasCHECK() {
        return hasCHECK;
    }
    public boolean isHasDEFAULT() {
        return hasDEFAULT;
    }
    public boolean isNOTNULL() {
        return isNOTNULL;
    }
    public boolean isUNIQUE() {
        return isUNIQUE;
    }
    public void setAutoIncrement(boolean isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }
    public void setHasCHECK(boolean hasCHECK) {
        this.hasCHECK = hasCHECK;
    }
    public void setHasDEFAULT(boolean hasDEFAULT) {
        this.hasDEFAULT = hasDEFAULT;
    }
    public void setNOTNULL(boolean isNOTNULL) {
        this.isNOTNULL = isNOTNULL;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }
    public void setUNIQUE(boolean isUNIQUE) {
        this.isUNIQUE = isUNIQUE;
    }

    @Override
    public String toString() {
        return ("Name - " + name + "\n" 
                + "isAutoIncrement - " + isAutoIncrement + "\n" 
                + "isPrimaryKey - " + isPrimaryKey + "\n"
                + "isUNIQUE - " + isUNIQUE + "\n"
                + "isNOTNULL - " + isNOTNULL + "\n"
                + "hasDEFAULT - " + hasDEFAULT + "\n"
                + "hasCHECK - " + hasCHECK + "\n");
    }
}
