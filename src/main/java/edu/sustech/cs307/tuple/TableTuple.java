package edu.sustech.cs307.tuple;

import edu.sustech.cs307.meta.TabCol;
import edu.sustech.cs307.meta.TableMeta;
import edu.sustech.cs307.record.Record;
import edu.sustech.cs307.value.Value;
import edu.sustech.cs307.value.ValueType;

import java.util.ArrayList;

import edu.sustech.cs307.meta.ColumnMeta;

import io.netty.buffer.ByteBuf;

public class TableTuple extends Tuple {
    private final String tableName;
    private final TableMeta tableMeta;
    private final Record record;

    public TableTuple(String tableName, TableMeta tableMeta, Record record) {
        this.tableName = tableName;
        this.tableMeta = tableMeta;
        this.record = record;
    }

    @Override
    public Value getValue(TabCol tabCol) {
        // TODO: throw a exception
        if (!tabCol.getTableName().equals(tableName)) {
            return null;
        }
        ColumnMeta columnMeta = tableMeta.getColumnMeta(tabCol.getColumnName());
        if (columnMeta == null) {
            return null;
        }
        int offset = columnMeta.getOffset();
        int len = columnMeta.getLen();
        // Use GetColumnValue to get the value based on offset and len
        ByteBuf columnValueBuf = record.GetColumnValue(offset, len); // Use the record passed to getValue
        // Convert ByteBuf to Value (assuming you have a method for this)
        return convertByteBufToValue(columnValueBuf, columnMeta.type);
    }

    private Value convertByteBufToValue(ByteBuf byteBuf, ValueType columnType) {
        byte[] bytes = new byte[byteBuf.readableBytes()];

        if (columnType == ValueType.INTEGER) {
            return new Value(byteBuf.getLong(0));
        } else if (columnType == ValueType.CHAR) {
            return new Value(byteBuf.getCharSequence(0, 64, java.nio.charset.StandardCharsets.UTF_8).toString());
        } else if (columnType == ValueType.FLOAT) {
            return new Value(byteBuf.getDouble(0));
        } else {
            // TODO: throw an exception
            return null; // Or throw an exception for unsupported types
        }
    }

    @Override
    public TabCol[] getTupleSchema() {
        ArrayList<TabCol> result = new ArrayList<TabCol>();
        // [this.tableMeta.getColumns().size()];
        this.tableMeta.getColumns().values().stream().forEach(columnMeta -> {
            TabCol tabCol = new TabCol(columnMeta.tableName, columnMeta.name);
            result.add(tabCol);
        });
        return (TabCol[]) result.toArray();

    }

    @Override
    public Value[] getValues() {
        // 通过 meta 顺序和信息获取所有 Value
        ArrayList<Value> values = new ArrayList<>();
        for (ColumnMeta columnMeta : this.tableMeta.getColumns().values()) {
            TabCol tabCol = new TabCol(columnMeta.tableName, columnMeta.name);
            Value value = getValue(tabCol);
            values.add(value);
        }
        return values.toArray(new Value[0]);
    }
}
