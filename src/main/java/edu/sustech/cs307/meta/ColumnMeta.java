package edu.sustech.cs307.meta;

import edu.sustech.cs307.value.*;


public class ColumnMeta {
    public String name;
    public ValueType type;

    public ColumnMeta(String name, ValueType type) {
        this.name = name;
        this.type = type;
    }
}