package edu.sustech.cs307.meta;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.exception.ExceptionTypes;

public class MetaManager {
    private static final String META_FILE = "meta_data.json";
    private final Map<String, TableMeta> tables;
    private final Gson gson;

    public MetaManager() throws DBException {
        this.gson = new Gson();
        this.tables = new HashMap<>();
        loadFromJson();
    }

    public void createTable(String tableName, TableMeta tableMeta) throws DBException {
        if (tables.containsKey(tableName)) {
            throw new DBException(ExceptionTypes.TableAlreadyExist(tableName));
        }
        tables.put(tableName, tableMeta);
        saveToJson();
    }

    public void dropTable(String tableName) throws DBException {
        if (!tables.containsKey(tableName)) {
            throw new DBException(ExceptionTypes.TableDoseNotExist(tableName));
        }
        tables.remove(tableName);
        saveToJson();
    }

    public void addColumnInTable(String tableName, ColumnMeta column) throws DBException {
        if (!tables.containsKey(tableName)) {
            throw new DBException(ExceptionTypes.TableDoseNotExist(tableName));
        }
        this.tables.get(tableName).addColumn(column);
    }

    public void dropColumnInTable(String tableName, String columnName) throws DBException {
        if (!tables.containsKey(tableName)) {
            throw new DBException(ExceptionTypes.TableDoseNotExist(tableName));
        }
        this.tables.get(tableName).dropColumn((columnName));
    }

    public TableMeta getTable(String tableName) {
        if (tables.containsKey(tableName)) {
            return tables.get(tableName);
        }
        return null;
    }

    public Set<String> getTableNames() {
        return this.tables.keySet();
    }

    private void saveToJson() throws DBException {
        try (Writer writer = new FileWriter(META_FILE)) {
            gson.toJson(tables, writer);
        } catch (Exception e) {
            throw new DBException(ExceptionTypes.UnableSaveMetadata(e.getMessage()));
        }
    }

    private void loadFromJson() throws DBException {
        File file = new File(META_FILE);
        if (!file.exists()) return;

        try (Reader reader = new FileReader(META_FILE)) {
            Type type = new TypeToken<Map<String, TableMeta>>() {
            }.getType();
            Map<String, TableMeta> loadedTables = gson.fromJson(reader, type);
            if (loadedTables != null) {
                tables.putAll(loadedTables);
            }
        } catch (Exception e) {
            throw new DBException(ExceptionTypes.UnableLoadMetadata(e.getMessage()));
        }
    }
}