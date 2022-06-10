package com.dbase;

import java.util.Arrays;

public enum DataBaseFieldTypes {

    VARCHAR,
    INTEGER,
    DOUBLE,
    // DATE,
    // REAL,
    // CHAR,
    // BINARY,
    // LONGVARCHAR,
    // BIT,
    // NUMERIC,
    // TINYINT,
    // SMALLINT,
    // BIGINT,
    // FLOAT,
    // VARBINARY,
    // TIME,
    // TIMESTAMP,
    // CLOB,
    // BLOB,
    // ARRAY,
    // REF,
    // STRUCT
    ;

    public static String[] getNames() {
        return Arrays.stream(DataBaseFieldTypes.class.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}