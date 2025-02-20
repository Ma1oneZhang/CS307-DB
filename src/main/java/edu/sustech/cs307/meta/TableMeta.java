package edu.sustech.cs307.meta;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.exception.ExceptionTypes;
import net.sf.jsqlparser.schema.Column;

import java.util.HashMap;
import java.util.Map;

public class TableMeta {
    public String tableName;
    public Map<String, ColumnMeta> columns; // 列名 -> 列的元数据

    public TableMeta(String tableName) {
        this.tableName = tableName;
        this.columns = new HashMap<>();
    }

    public TableMeta(String tableName, Map<String, ColumnMeta> columns){
        this.tableName = tableName;
        this.columns = columns;
    }

    public void addColumn(ColumnMeta column) throws DBException {
        String columnName = column.name;
        if (this.columns.containsKey(columnName)){
            throw new DBException(ExceptionTypes.ColumnAlreadyExist(columnName));
        }
        this.columns.put(columnName, column);
    }

    public void dropColumn(String columnName) throws DBException{
        if (!this.columns.containsKey(columnName)){
            throw new DBException(ExceptionTypes.ColumnDoseNotExist(columnName));
        }
        this.columns.remove(columnName);
    }

    public ColumnMeta getColumnMeta(String columnName){
        if (this.columns.containsKey(columnName)){
            return this.columns.get(columnName);
        }
        return null;
    }

    public Map<String, ColumnMeta> getColumns(){
        return this.columns;
    }

    public void setColumns(Map<String, ColumnMeta> columns){
        this.columns = columns;
    }

}
