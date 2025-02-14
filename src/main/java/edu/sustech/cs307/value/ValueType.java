package edu.sustech.cs307.value;

public enum ValueType {
    CHAR,
    INTEGER,
    FLOAT;

    @Override
    public String toString(){
        return switch (this) {
            case CHAR -> "char";
            case INTEGER -> "int";
            case FLOAT -> "float";
        };
    }
}
